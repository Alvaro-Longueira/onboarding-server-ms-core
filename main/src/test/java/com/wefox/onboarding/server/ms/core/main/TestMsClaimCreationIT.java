package com.wefox.onboarding.server.ms.core.main;

import static com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.Bindings.topic;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import com.wefox.onboarding.server.ms.core.application.util.ApplicationUtils;
import com.wefox.onboarding.server.ms.core.domain.enums.ErrorDetailsEnum;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.account.AccountWebClient;
import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.Bindings;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.response.ClaimResponse;
import com.wefox.onboarding.server.ms.core.main.data.ClaimFactory;
import com.wefox.onboarding.server.ms.core.main.test.IntegrationTest;
import com.wefox.onboarding.server.ms.core.main.test.MockAvroDeserializer;
import com.wefox.onboarding.server.ms.core.main.test.PostgreSqlContainer;
import com.wefox.server.lib.common.core.exception.BusinessException;
import com.wefox.server.lib.common.stream.kafka.test.EmbeddedKafkaTestHelper;
import com.wefox.server.spec.avro.claims.entity.ClaimDTO;
import com.wefox.server.spec.avro.claims.enums.ClaimStatus;
import io.restassured.RestAssured;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.test.context.jdbc.Sql;

@Slf4j
@IntegrationTest
@Sql({"/sql/cleaning_db.sql", "/sql/initial_claim_type_settings.sql"})
class TestMsClaimCreationIT extends PostgreSqlContainer {

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

  @MockBean AccountWebClient accountWebClient;

  @Autowired private ClaimFactory factory;
  @Autowired private EmbeddedKafkaBroker embeddedKafka;
  @Autowired private ApplicationUtils utils;
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
    Mockito.when(accountWebClient.getAccount(any())).thenReturn(HttpStatus.OK);
  }

  @Test
  void validate_create_claim_success_201() {
    create_claim_201(claimType2);
  }

  @Test
  void validate_create_claim_conflict_409() {
    var claim = create_claim_201(claimType);

    // then validate that unique Claim Id restriction
    given()
        .header("Content-Type", "application/json")
        .body(claim)
        .when()
        .post("/claims")
        .then()
        .assertThat()
        .statusCode(409);
  }

  @Test
  void validate_create_claim_only_with_mandatory_fields() {
    Map<String, Object> claim = new HashMap<>();
    claim.put("claim_id", "IT_CLM_" + randomSuffix);
    claim.put("account_id", accountId);
    claim.put("type", claimType);
    claim.put("notification_date", OffsetDateTime.now().minusDays(21));
    // this field is not mandatory per se, but is the minimum data required
    claim.put("contract_id", contractId);

    given()
        .header("Content-Type", "application/json")
        .body(claim)
        .when()
        .post("/claims")
        .then()
        .assertThat()
        .statusCode(201);
  }

  @Test
  void validate_create_claim_offer_id_contract_id_422() {
    var claim = factory.generateClaim(randomSuffix, claimType, accountId, contractId, false);

    given()
        .header("Content-Type", "application/json")
        .body(claim)
        .when()
        .post("/claims")
        .then()
        .assertThat()
        .statusCode(422)
        .contentType("application/json");
  }

  @Test
  void validate_create_claim_fails_by_external_service_but_it_is_saved_without_publish_event() {
    Mockito.when(accountWebClient.getAccount(any()))
        .thenThrow(new BusinessException(ErrorDetailsEnum.ACCOUNT_NOT_FOUND));

    var claim = factory.generateClaim(randomSuffix, claimType2, accountId, contractId, false);
    String entityId = "IT_CLM_" + randomSuffix;
    claim.put("claim_id", entityId);
    claim.remove("offer_id");
    claim.remove("coverage_id");

    given()
        .header("Content-Type", "application/json")
        .body(claim)
        .when()
        .post("/claims")
        .then()
        .assertThat()
        .statusCode(422)
        .contentType("application/json")
        .extract()
        .response();

    ClaimResponse claimResponse =
        given()
            .header("Content-Type", "application/json")
            .when()
            .get("/claims/" + entityId)
            .then()
            .assertThat()
            .statusCode(200)
            .contentType("application/json")
            .extract()
            .response()
            .as(ClaimResponse.class);
    assertEquals(entityId, claimResponse.getClaimId());
    assertNull(claimResponse.getStatus());

    var records =
        kafkaTestHelper.getRecordsByKey(
            kafkaConsumer, topic(Bindings.CLAIM_CHANGED_EVENT), entityId, 0, 0);
    assertEquals(0, records.size());
  }

  @Test
  void validate_create_claim_contains_market_header() {
    var claim = factory.generateClaim(randomSuffix, claimType, accountId, contractId, true);
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
    var records =
        kafkaTestHelper.getRecordsByKey(
            kafkaConsumer, topic(Bindings.CLAIM_CHANGED_EVENT), entityId, 1, 1);

    assertEquals(1, records.size());
    var record = records.stream().findFirst();
    assertTrue(record.isPresent());
    var headerIsPresent =
        StreamSupport.stream(record.get().headers().spliterator(), false)
            .anyMatch(header -> "x-market".equals(header.key()));
    assertTrue(headerIsPresent);
  }

  private void validate_create_claim_success_201_message_received_with_coverage(String claimType) {
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
    dtoAssert.setStatus(ClaimStatus.CREATED);
    dtoAssert.setType(claimType);
    dtoAssert.setSymassId("DE" + randomSuffix);

    validate_default_claim_content(payload, dtoAssert);
  }

  private void validate_create_claim_success_201_message_received_without_coverage(
      String claimType) {
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

  private Map<String, Object> create_claim_201(String claimType) {
    assertFalse(utils.skipSymass());
    assertFalse(utils.noSendEvents());

    var claim = factory.generateClaim(randomSuffix, claimType, accountId, contractId, false);
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

    validate_create_claim_success_201_message_received_without_coverage(claimType);

    return claim;
  }
}
