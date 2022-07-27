package com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.entity.ClaimDBEntity;
import com.wefox.server.lib.common.core.mapper.DateMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;

class DBClaimMapperTests {

  private final EasyRandom generator = new EasyRandom();
  private final DBClaimMapper mapper = new DBClaimMapperImpl();

  @Test
  void toDomainEntity__givenAnyClaimDBEntity_whenMap_thenCorrectlyMapsData() {
    ClaimDBEntity entity = generator.nextObject(ClaimDBEntity.class);
    Claim domain = mapper.toDomainEntity(entity);

    assertEquals(DateMapper.toOffsetDateTime(entity.getCreatedAt()), domain.getCreatedAt());
    assertEquals(DateMapper.toOffsetDateTime(entity.getUpdatedAt()), domain.getUpdatedAt());
    assertEqualsAllFields(domain, entity);
  }

  @Test
  void toDBEntity__givenAnyClaim_whenMap_thenCorrectlyMapsData() {
    Claim domain = generator.nextObject(Claim.class);
    ClaimDBEntity entity = mapper.toDBEntity(domain);

    assertEquals(entity.getCreatedAt(), DateMapper.toLocalDateTime(domain.getCreatedAt()));
    assertEquals(entity.getUpdatedAt(), DateMapper.toLocalDateTime(domain.getUpdatedAt()));
    assertEqualsAllFields(domain, entity);
  }

  private void assertEqualsAllFields(Claim domain, ClaimDBEntity entity) {
    assertAll(
        () -> assertEquals(entity.getId(), domain.getInternalId()),
        () -> assertEquals(entity.getClaimId(), domain.getId()),
        () -> assertEquals(entity.getEventDate(), domain.getEventDate().orElse(null)),
        () -> assertEquals(entity.getEntryDate(), domain.getEntryDate()),
        () -> assertEquals(entity.getNotificationDate(), domain.getNotificationDate()),
        () -> assertEquals(entity.getDescription(), domain.getDescription().orElse(null)),
        () -> assertEquals(entity.getPlaceOfEvent(), domain.getPlaceOfEvent().orElse(null)),
        () -> assertEquals(entity.getContractId(), domain.getContractId().orElse(null)),
        () -> assertEquals(entity.getOfferId(), domain.getOfferId().orElse(null)),
        () -> assertEquals(entity.getAccountId(), domain.getAccountId()),
        () -> assertEquals(entity.getCoverageId(), domain.getCoverageId().orElse(null)),
        () -> assertEquals(entity.getStatus().toString(), domain.getStatus().toString()),
        () -> assertEquals(entity.getProductId(), domain.getProductId().orElse(null)),
        () -> assertEquals(entity.getInsuranceId(), domain.getInsuranceId().orElse(null)),
        () -> assertEquals(entity.getWithoutCoverage(), domain.getWithoutCoverage()),
        () -> assertEquals(entity.getExternalId(), domain.getSymassId().orElse(null)),
        () -> assertEquals(entity.getVersion(), domain.getVersion()),
        () -> assertEquals(entity.getCreatedBy(), domain.getCreatedBy()),
        () -> assertEquals(entity.getUpdatedBy(), domain.getUpdatedBy()));
  }
}
