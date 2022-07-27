package com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.wefox.onboarding.server.ms.core.domain.entity.ClaimTypeSetting;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.entity.ClaimTypeSettingDBEntity;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.mapper.DBClaimTypeSettingMapper;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.mapper.DBClaimTypeSettingMapperImpl;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DBClaimTypeSettingRepositoryTests {

  @Mock private SpringDataClaimTypeSettingRepository springDataRepository;
  @Spy private DBClaimTypeSettingMapper mapper = new DBClaimTypeSettingMapperImpl();

  @InjectMocks private DBClaimTypeSettingRepository repository;

  @Test
  void Given_a_claimType_When_findCoverageType_Then_return_coverageType() {
    // Given
    final Random random = new Random();
    String claimType = "070101";
    String coverageType = "Glass damage";
    String insuredEventClass = "CAR";
    final ClaimTypeSettingDBEntity claimTypeSetting =
        ClaimTypeSettingDBEntity.builder()
            .id(random.nextLong())
            .keyNumberClaimType(claimType)
            .insuredEventClass(insuredEventClass)
            .claimType("Glass damage - Windshield")
            .coverageType(coverageType)
            .build();
    when(springDataRepository.findByKeyNumberClaimType(any()))
        .thenReturn(Collections.singletonList(claimTypeSetting));

    // When
    Optional<ClaimTypeSetting> result = repository.findCoverageType(claimType);

    // Then
    assertTrue(result.isPresent(), "coverageType found");
    assertEquals(coverageType, result.get().getCoverageType(), "coverageType");
    assertEquals(insuredEventClass, result.get().getInsuredEventClass(), "insuredEventClass");
  }

  @Test
  void
      Given_an_unexisting_claimType_When_findCoverageType_Then_return_empty_optionalGiven_a_coverageType_When_findCoverageType_Then_return_coverageType() {
    // Given
    String claimType = "claim_1";
    when(springDataRepository.findByKeyNumberClaimType(any())).thenReturn(Collections.emptyList());

    // When
    Optional<ClaimTypeSetting> result = repository.findCoverageType(claimType);

    // Then
    assertFalse(result.isPresent(), "coverageType found");
  }
}
