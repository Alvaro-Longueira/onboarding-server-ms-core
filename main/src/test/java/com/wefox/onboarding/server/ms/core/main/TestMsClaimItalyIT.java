package com.wefox.onboarding.server.ms.core.main;

import static com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.Bindings.topic;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wefox.onboarding.server.ms.core.application.util.ApplicationUtils;
import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.Bindings;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.common.ClaimStatusDTO;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.response.ClaimResponse;
import com.wefox.onboarding.server.ms.core.main.data.ClaimFactory;
import com.wefox.onboarding.server.ms.core.main.test.IntegrationTest;
import com.wefox.onboarding.server.ms.core.main.test.MockAvroDeserializer;
import com.wefox.onboarding.server.ms.core.main.test.PostgreSqlContainer;
import com.wefox.server.lib.common.stream.kafka.test.EmbeddedKafkaTestHelper;
import io.restassured.RestAssured;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@Slf4j
@IntegrationTest
@Sql({"/sql/cleaning_db.sql", "/sql/initial_claim_type_settings.sql"})
@TestPropertySource(
    properties = {
      "wefox.application.marketId=it",
      "wefox.application.symassSkip=de,it",
      "wefox.application.noSendEvents=de,it"
    })
class TestMsClaimItalyIT extends PostgreSqlContainer {

  @LocalServerPort private int port;

  // defined by /sql/initial_claim_type_settings.sql as key_number_claim_type
  private final String claimType = "070101";
  // used in Wiremock stubs
  private final String accountId = "DE398748";
  private final String contractId = "126967";
  private final String[] topicNames = new String[] {"topic-claim-changed-event"};
  private long randomSuffix;

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
  }

  @Test
  void validate_create_claim_with_config_for_italy() {
    assertTrue(utils.skipSymass());
    assertTrue(utils.noSendEvents());

    var claim = factory.generateClaim(randomSuffix, claimType, accountId, contractId, false);
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
        .statusCode(201)
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
    assertEquals(ClaimStatusDTO.CREATED, claimResponse.getStatus());

    var records =
        kafkaTestHelper.getRecordsByKey(
            kafkaConsumer, topic(Bindings.CLAIM_CHANGED_EVENT), entityId, 0, 0);
    assertEquals(0, records.size());
  }
}
