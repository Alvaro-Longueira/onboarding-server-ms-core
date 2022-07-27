package com.wefox.onboarding.server.ms.core.main;

import static com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.Bindings.topic;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.Bindings;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.response.ClaimResponse;
import com.wefox.onboarding.server.ms.core.main.data.ClaimFactory;
import com.wefox.onboarding.server.ms.core.main.test.IntegrationTest;
import com.wefox.onboarding.server.ms.core.main.test.MockAvroDeserializer;
import com.wefox.onboarding.server.ms.core.main.test.PostgreSqlContainer;
import com.wefox.server.lib.common.stream.kafka.test.EmbeddedKafkaTestHelper;
import com.wefox.server.spec.avro.claims.entity.ClaimDTO;
import com.wefox.server.spec.avro.claims.enums.ClaimStatus;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.time.OffsetDateTime;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.test.context.jdbc.Sql;

@Slf4j
@IntegrationTest
@Sql({
  "/sql/cleaning_db.sql",
  "/sql/initial_claim_type_settings.sql",
  "/sql/initial_claims_settings.sql"
})
class TestMsClaimUpdateIT extends PostgreSqlContainer {

  @LocalServerPort private int port;

  // defined by /sql/initial_claim_type_settings.sql as key_number_claim_type
  private final String claimType = "070101";
  private final String claimType2 = "040400";
  // used in Wiremock stubs
  private final String accountId = "DE398748";
  private final String contractId = "126967";
  private final String insuranceIdFromContract = "65269";
  private final String productIdFromContract = "1101";
  private final String productDescFromContract = "Product Description";
  private final String[] topicNames = new String[] {"topic-claim-changed-event"};

  private long randomSuffix;

  @Autowired private ClaimFactory factory;
  @Autowired private EmbeddedKafkaBroker embeddedKafka;
  private EmbeddedKafkaTestHelper kafkaTestHelper;
  private Consumer<String, ?> kafkaConsumer;

  @PostConstruct
  public void setup() /**/ {
    RestAssured.port = port;
    kafkaTestHelper = new EmbeddedKafkaTestHelper(topicNames, embeddedKafka);
    kafkaConsumer =
        kafkaTestHelper.buildConsumer(new StringDeserializer(), new MockAvroDeserializer());
  }

  @BeforeEach
  public void beforeEach() {
    randomSuffix = RandomUtils.nextLong(1_000_000, 2_000_000);
  }

  @Test
  void validate_update_description_and_place_of_event_success_200() {
    var claim = create_claim_201(claimType2, true);
    String claimId = "IT_CLM_" + randomSuffix;

    factory.updateDescription(claim, "IT_DescriptionUpdated" + randomSuffix);
    factory.updatePlaceOfEvent(claim, "IT_PlaceOfEventUpdated" + randomSuffix);

    ClaimResponse response =
        given()
            .header("Content-Type", "application/json")
            .body(claim)
            .when()
            .put(String.format("/claims/%s", claimId))
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .as(ClaimResponse.class);

    assertThat(claim.get("description")).isEqualTo(response.getDescription());
    assertThat(claim.get("place_of_event")).isEqualTo(response.getPlaceOfEvent());

    valid_update_description_and_place_of_event_success_200_message_received();
  }

  @Test
  void validate_update_without_coverage_success_200() {
    var claim = create_claim_201(claimType2, true);
    String claimId = "IT_CLM_" + randomSuffix;

    factory.updateWithoutCoverage(claim, Boolean.FALSE);

    ClaimResponse response =
        given()
            .header("Content-Type", "application/json")
            .body(claim)
            .when()
            .put(String.format("/claims/%s", claimId))
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .as(ClaimResponse.class);

    assertThat(claim.get("without_coverage")).isEqualTo(response.getWithoutCoverage());
  }

  @Test
  void validate_update_claim_status_success_200() {
    create_claim_201(claimType2, true);
    String claimId = "IT_CLM_" + randomSuffix;
    String status = "UNDER_REVIEW";
    var request = factory.generateStatus(status);

    ClaimResponse response =
        given()
            .header("Content-Type", "application/json")
            .body(request)
            .when()
            .put(String.format("/claims/%s/status", claimId))
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .as(ClaimResponse.class);

    assertThat(status).isEqualTo(response.getStatus().toString());

    validate_update_claim_status_success_200_message_received();
  }

  @Test
  void validate_update_status_invalid_transition_422() {
    String claimId = "claimPaidOut";
    String status = "REJECTED";
    var request = factory.generateStatus(status);

    Response response =
        given()
            .header("Content-Type", "application/json")
            .body(request)
            .when()
            .put(String.format("/claims/%s/status", claimId))
            .then()
            .assertThat()
            .statusCode(422)
            .contentType("application/json")
            .extract()
            .response();

    log.info("response = " + response.prettyPrint());
  }

  @Test
  void validate_update_claim_success_200() {
    var claim = create_claim_201(claimType2, false);
    String claimId = "IT_CLM_" + randomSuffix;

    factory.updateEventDate(claim, OffsetDateTime.now().minusDays(15));
    factory.updateNotificationDate(claim, OffsetDateTime.now().minusDays(11));
    factory.updateType(claim, claimType);

    var response =
        given()
            .header("Content-Type", "application/json")
            .body(claim)
            .when()
            .put("/claims/" + claimId)
            .then()
            .assertThat()
            .statusCode(200)
            .extract()
            .as(ClaimResponse.class);

    log.info("response = " + response);

    assertThat(response.getClaimId()).isEqualTo(claimId);
    assertThat(response.getEventDate()).isEqualTo(claim.get("event_date"));
    assertThat(response.getNotificationDate()).isEqualTo(claim.get("notification_date"));
    assertThat(response.getType()).isEqualTo(claimType);

    // validate claim sent on topic
    validate_update_claim_success_200_message_received();
  }

  @Test
  void validate_update_claim_by_id_not_found() {
    String claimId = RandomStringUtils.random(10, true, true);

    var claim = factory.generateClaim(randomSuffix, claimType2, accountId, contractId, true);

    Response response =
        given()
            .header("Content-Type", "application/json")
            .body(claim)
            .when()
            .get("/claims/" + claimId)
            .then()
            .assertThat()
            .statusCode(404)
            .contentType("application/json")
            .extract()
            .response();

    log.info("response = " + response.prettyPrint());
  }

  @Test
  void validate_update_claim_wrong_dates() {
    var claim = create_claim_201(claimType, false);
    String claimId = "IT_CLM_" + randomSuffix;

    factory.updateEventDate(claim, OffsetDateTime.now().plusYears(5));
    factory.updateNotificationDate(claim, OffsetDateTime.now().minusDays(11));
    factory.updateType(claim, claimType2);

    // Update wrong data claim
    given()
        .header("Content-Type", "application/json")
        .body(claim)
        .when()
        .put("/claims/" + claimId)
        .then()
        .assertThat()
        .statusCode(422)
        .contentType("application/json");
  }

  @Test
  void validate_update_claim_bad_request() {
    String claimId = "IT_CLM_" + randomSuffix;

    var claim = factory.generateClaim(randomSuffix, claimType2, accountId, contractId, true);
    claim.replace("event_date", "xxxxxx-02-27T02:44:42.009420+01:00");

    given()
        .header("Content-Type", "application/json")
        .body(claim)
        .when()
        .put("/claims/" + claimId)
        .then()
        .assertThat()
        .statusCode(400)
        .contentType("application/json");
  }

  // duplicate
  private void validate_update_claim_success_200_message_received() {
    String entityId = "IT_CLM_" + randomSuffix;
    var payload =
        kafkaTestHelper.getMessageEvent(
            kafkaConsumer, topic(Bindings.CLAIM_CHANGED_EVENT), entityId, ClaimDTO.class, 1, 1);

    var dtoAssert = new ClaimDTO();
    dtoAssert.setId(entityId);
    dtoAssert.setAccountId(accountId);
    dtoAssert.setContractId(contractId);
    dtoAssert.setDescription("IT_Description" + randomSuffix);
    dtoAssert.setPlaceOfEvent("IT_PlaceOfEvent" + randomSuffix);
    dtoAssert.setInsuranceId(insuranceIdFromContract);
    dtoAssert.setProductId(productIdFromContract);
    dtoAssert.setProductDescription(productDescFromContract);
    dtoAssert.setStatus(ClaimStatus.CREATED);
    dtoAssert.setType(claimType);
    dtoAssert.setSymassId("DE" + randomSuffix);

    validate_default_claim_content(payload, dtoAssert);
  }

  private void valid_update_description_and_place_of_event_success_200_message_received() {
    String entityId = "IT_CLM_" + randomSuffix;
    var payload =
        kafkaTestHelper.getMessageEvent(
            kafkaConsumer, topic(Bindings.CLAIM_CHANGED_EVENT), entityId, ClaimDTO.class);

    var dtoAssert = new ClaimDTO();
    dtoAssert.setId(entityId);
    dtoAssert.setAccountId(accountId);
    dtoAssert.setContractId(contractId);
    dtoAssert.setDescription("IT_DescriptionUpdated" + randomSuffix);
    dtoAssert.setPlaceOfEvent("IT_PlaceOfEventUpdated" + randomSuffix);
    dtoAssert.setProductId("IT_PDT_" + randomSuffix);
    dtoAssert.setProductDescription(productDescFromContract);
    dtoAssert.setStatus(ClaimStatus.CREATED);
    dtoAssert.setType(claimType2);
    dtoAssert.setSymassId("DE" + randomSuffix);

    validate_default_claim_content(payload, dtoAssert);
  }

  private void validate_update_claim_status_success_200_message_received() {
    String entityId = "IT_CLM_" + randomSuffix;
    var payload =
        kafkaTestHelper.getMessageEvent(
            kafkaConsumer, topic(Bindings.CLAIM_CHANGED_EVENT), entityId, ClaimDTO.class, 1, 1);

    var dtoAssert = new ClaimDTO();
    dtoAssert.setId(entityId);
    dtoAssert.setAccountId(accountId);
    dtoAssert.setContractId(contractId);
    dtoAssert.setDescription("IT_Description" + randomSuffix);
    dtoAssert.setPlaceOfEvent("IT_PlaceOfEvent" + randomSuffix);
    dtoAssert.setProductId("IT_PDT_" + randomSuffix);
    dtoAssert.setProductDescription(productDescFromContract);
    dtoAssert.setStatus(ClaimStatus.UNDER_REVIEW);
    dtoAssert.setType(claimType2);
    dtoAssert.setSymassId("DE" + randomSuffix);

    validate_default_claim_content(payload, dtoAssert);
  }

  private void validate_default_claim_content(ClaimDTO payload, ClaimDTO dtoAssert) {
    assertAll(
        () -> assertEquals(dtoAssert.getDescription(), payload.getDescription(), "description"),
        () -> assertEquals(dtoAssert.getPlaceOfEvent(), payload.getPlaceOfEvent(), "placeOfEvent"),
        () -> assertEquals(dtoAssert.getId(), payload.getId(), "id"),
        () -> assertEquals(dtoAssert.getContractId(), payload.getContractId(), "contractId"),
        () -> assertEquals(dtoAssert.getInsuranceId(), payload.getInsuranceId(), "insuranceId"),
        () -> assertEquals(dtoAssert.getCoverageId(), payload.getCoverageId(), "coverageId"),
        () -> assertEquals(dtoAssert.getProductId(), payload.getProductId(), "productId"),
        () -> assertEquals(dtoAssert.getAccountId(), payload.getAccountId(), "accountId"),
        () -> assertEquals(dtoAssert.getStatus(), payload.getStatus(), "status"),
        () -> assertEquals(dtoAssert.getType(), payload.getType(), "type"),
        () -> assertEquals(dtoAssert.getSymassId(), payload.getSymassId(), "symassId"));
  }

  private Map<String, Object> create_claim_201(String claimType, boolean withoutCoverage) {
    var claim =
        factory.generateClaim(randomSuffix, claimType, accountId, contractId, withoutCoverage);
    claim.remove("offer_id");
    claim.remove("coverage_id");

    given()
        .header("Content-Type", "application/json")
        .body(claim)
        .when()
        .post("/claims")
        .then()
        .assertThat()
        .statusCode(201)
        .contentType("application/json")
        .extract()
        .response();

    String entityId = "IT_CLM_" + randomSuffix;
    kafkaTestHelper.getMessageEvent(
        kafkaConsumer, topic(Bindings.CLAIM_CHANGED_EVENT), entityId, ClaimDTO.class, 1, 1);

    return claim;
  }
}
