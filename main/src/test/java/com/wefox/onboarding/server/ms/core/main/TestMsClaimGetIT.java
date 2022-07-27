package com.wefox.onboarding.server.ms.core.main;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.response.ClaimResponse;
import com.wefox.onboarding.server.ms.core.main.data.ClaimFactory;
import com.wefox.onboarding.server.ms.core.main.test.IntegrationTest;
import com.wefox.onboarding.server.ms.core.main.test.MockAvroDeserializer;
import com.wefox.onboarding.server.ms.core.main.test.PostgreSqlContainer;
import com.wefox.server.lib.common.api.pagination.PageDTO;
import com.wefox.server.lib.common.stream.kafka.test.EmbeddedKafkaTestHelper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.StringDeserializer;
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
class TestMsClaimGetIT extends PostgreSqlContainer {

  @LocalServerPort private int port;

  private final String[] topicNames = new String[] {"topic-claim-changed-event"};
  private final Long totalElements = 7L;

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

  @Test
  void validate_get_claim_by_id() {
    final String claimId = "claim_id_1";

    Response response =
        given()
            .header("Content-Type", "application/json")
            .when()
            .get("/claims/" + claimId)
            .then()
            .assertThat()
            .statusCode(200)
            .contentType("application/json")
            .extract()
            .response();

    ClaimResponse claimResponse = response.body().as(ClaimResponse.class);
    assertThat(claimResponse.getClaimId()).isEqualTo(claimId);

    log.info("response = " + response.prettyPrint());
  }

  @Test
  void validate_get_claim_by_id_migrated() {
    final String claimId = "claimMigrated";

    ClaimResponse claimResponse =
        given()
            .header("Content-Type", "application/json")
            .when()
            .get("/claims/" + claimId)
            .then()
            .assertThat()
            .statusCode(200)
            .contentType("application/json")
            .extract()
            .body()
            .as(ClaimResponse.class);

    assertThat(claimResponse.getClaimId()).isEqualTo(claimId);
    assertNull(claimResponse.getProductId());
    assertThat(claimResponse.getProductDescription()).isEqualTo("product_description");
  }

  @Test
  void validate_get_claim_by_id_not_found() {
    String claimId = RandomStringUtils.random(10, true, true);

    Response response =
        given()
            .header("Content-Type", "application/json")
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
  void validate_get_claims_basic() {
    Response response =
        given()
            .header("Content-Type", "application/json")
            .when()
            .get("/claims")
            .then()
            .assertThat()
            .statusCode(200)
            .contentType("application/json")
            .extract()
            .response();

    log.info("response = " + response.prettyPrint());

    PageDTO page = response.body().as(PageDTO.class);

    assertThat(page.getTotalElements()).isEqualTo(totalElements);
    assertThat(page.getPageSize()).isEqualTo(25);
    assertThat(page.getPageNumber()).isZero();
    assertThat(page.getTotalPages()).isEqualTo(1);
    assertThat(page.getContent().size()).isEqualTo(totalElements.intValue());
  }

  @Test
  void validate_get_claims_pagination() {
    Response response =
        given()
            .header("Content-Type", "application/json")
            .when()
            .get("/claims?page_number=0&page_size=1")
            .then()
            .assertThat()
            .statusCode(200)
            .contentType("application/json")
            .extract()
            .response();

    log.info("response = " + response.prettyPrint());

    PageDTO page = response.body().as(PageDTO.class);

    assertThat(page.getTotalElements()).isEqualTo(totalElements);
    assertThat(page.getPageSize()).isEqualTo(1);
    assertThat(page.getPageNumber()).isZero();
    assertThat(page.getTotalPages()).isEqualTo(totalElements.intValue());
    assertThat(page.getContent().size()).isEqualTo(1);

    // TODO: validate output format dateTimes

    response =
        given()
            .header("Content-Type", "application/json")
            .when()
            .get("/claims?page_number=1&page_size=1")
            .then()
            .assertThat()
            .statusCode(200)
            .contentType("application/json")
            .extract()
            .response();

    log.info("response = " + response.prettyPrint());

    page = response.body().as(PageDTO.class);

    assertThat(page.getTotalElements()).isEqualTo(totalElements);
    assertThat(page.getPageSize()).isEqualTo(1);
    assertThat(page.getPageNumber()).isEqualTo(1);
    assertThat(page.getTotalPages()).isEqualTo(totalElements.intValue());
    assertThat(page.getContent().size()).isEqualTo(1);
  }

  @Test
  void validate_get_claims_by_account_id_filter() {
    Response response =
        given()
            .header("Content-Type", "application/json")
            .when()
            .get("/claims?account_id=account_id_1")
            .then()
            .assertThat()
            .statusCode(200)
            .contentType("application/json")
            .extract()
            .response();

    log.info("response = " + response.prettyPrint());

    PageDTO page = response.body().as(PageDTO.class);

    assertThat(page.getTotalElements()).isEqualTo(1);
    assertThat(page.getPageSize()).isEqualTo(25);
    assertThat(page.getPageNumber()).isZero();
    assertThat(page.getTotalPages()).isEqualTo((1));
    assertThat(page.getContent().size()).isEqualTo(1);
  }

  @Test
  void validate_get_claims_by_account_id_invalid_filter() {
    Response response =
        given()
            .header("Content-Type", "application/json")
            .when()
            .get("/claims?account_id=XXX")
            .then()
            .assertThat()
            .statusCode(200)
            .contentType("application/json")
            .extract()
            .response();

    log.info("response = " + response.prettyPrint());

    PageDTO page = response.body().as(PageDTO.class);

    assertThat(page.getTotalElements()).isZero();
    assertThat(page.getPageSize()).isEqualTo(25);
    assertThat(page.getPageNumber()).isZero();
    assertThat(page.getTotalPages()).isZero();
    assertThat(page.getContent().size()).isZero();
  }
}
