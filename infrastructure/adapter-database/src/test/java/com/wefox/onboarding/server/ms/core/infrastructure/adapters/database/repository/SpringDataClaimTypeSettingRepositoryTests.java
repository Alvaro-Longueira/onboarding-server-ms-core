package com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.annotation.H2JdbcTest;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.entity.ClaimTypeSettingDBEntity;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@H2JdbcTest
class SpringDataClaimTypeSettingRepositoryTests {

  @Autowired private SpringDataClaimTypeSettingRepository repository;

  @BeforeEach
  void init() {
    repository.saveAll(
        Arrays.asList(
            buildEntity("type_1", "coverage_1"),
            buildEntity("type_2", "coverage_2"),
            buildEntity("type_3", "coverage_3"),
            buildEntity("type_4", "coverage_4")));
  }

  @Test
  void
      Given_an_existing_claim_type_When_findByClaimType_Then_return_ClaimTypeSettingDBEntity_with_coverageType() {
    // Given
    final String claimType = "type_3";
    final String coverageType = "coverage_3";

    // When
    List<ClaimTypeSettingDBEntity> list = repository.findByKeyNumberClaimType(claimType);

    // Then
    assertEquals(1, list.size(), "ClaimTypeSettingDBEntityList.size");
    ClaimTypeSettingDBEntity entity = list.get(0);
    assertEquals(coverageType, entity.getCoverageType(), "ClaimTypeSettingDBEntity.coverageType");
  }

  @Test
  void Given_an_unexisting_claim_type_When_findByClaimType_Then_return_empty_list() {
    // Given
    final String claimType = "type_10"; // unexisting

    // When
    List<ClaimTypeSettingDBEntity> list = repository.findByKeyNumberClaimType(claimType);

    // Then
    assertTrue(list.isEmpty(), "ClaimTypeSettingDBEntityList.empty");
  }

  private static ClaimTypeSettingDBEntity buildEntity(String keyNumberClaimType, String coverage) {
    return ClaimTypeSettingDBEntity.builder()
        .keyNumberClaimType(keyNumberClaimType)
        .insuredEventClass("insuredEventClass")
        .claimType("claimType")
        .coverageType(coverage)
        .build();
  }
}
