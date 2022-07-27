package com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wefox.onboarding.server.ms.core.application.port.input.ClaimFilter;
import com.wefox.onboarding.server.ms.core.domain.enums.ClaimStatus;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.annotation.H2JdbcTest;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.entity.ClaimDBEntity;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.util.ClaimDBEntityFactory;
import com.wefox.server.lib.common.data.core.audit.entity.AbstractAuditableEntity;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;

@Slf4j
@H2JdbcTest
class SpringDataClaimRepositoryTests {

  private static final String CURRENT_AUDITOR = "SYSTEM";

  private final ClaimDBEntityFactory claimDBEntityFactory = new ClaimDBEntityFactory();
  @MockBean private AuditorAware<String> auditorAware;

  @Autowired private SpringDataClaimRepository repository;

  @BeforeEach
  public void init() {
    Mockito.when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(CURRENT_AUDITOR));
  }

  @Test
  void Given_a_claim_When_saved_Then_findById_return_the_same_claim() {
    // Given
    ClaimDBEntity originalEntity = claimDBEntityFactory.build();

    // When
    ClaimDBEntity savedEntity = repository.save(originalEntity);

    // Then
    Optional<ClaimDBEntity> entityOpt = repository.findById(savedEntity.getId());
    assertTrue(entityOpt.isPresent(), "entity not found");
    ClaimDBEntity entity = entityOpt.get();

    verifySameEntity(originalEntity, entity);
    verifyAuditFields(entity, CURRENT_AUDITOR);
  }

  @Test
  void
      Given_a_saved_claim_When_the_claim_updated_in_database_Then_the_version_number_is_incremented() {
    // Given
    ClaimDBEntity originalEntity = repository.save(claimDBEntityFactory.build());
    final Long originalId = originalEntity.getId();
    final Long originalVersion = originalEntity.getVersion();
    log.debug("Original version {}", originalVersion);

    // When
    String updater = "SOME_OTHER_GUY";
    Mockito.when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(updater));
    ClaimDBEntity tmpEntity = claimDBEntityFactory.build();
    tmpEntity.setId(originalEntity.getId());
    tmpEntity.setCreatedAt(originalEntity.getCreatedAt());
    tmpEntity.setCreatedBy(originalEntity.getCreatedBy());
    tmpEntity.setVersion(originalEntity.getVersion());
    tmpEntity.setStatus(ClaimStatus.REJECTED);
    final ClaimDBEntity updatedEntity = repository.save(tmpEntity);

    // Then
    verifyAuditFields(updatedEntity, updater);
    assertAll(
        "Additional checks",
        () -> assertEquals(originalId, updatedEntity.getId(), "wrong id"),
        () -> assertNotEquals(originalVersion, updatedEntity.getVersion(), "version"),
        () -> assertNotNull(updatedEntity.getUpdatedAt(), "getUpdatedAt()"),
        () ->
            assertNotEquals(
                originalEntity.getUpdatedAt(), updatedEntity.getUpdatedAt(), "getUpdatedAt()"));
  }

  @Test
  void Given_a_saved_claim_When_findByFilter_contractId_Then_returns_the_same_claim() {
    // Given
    ClaimDBEntity savedEntity = repository.save(claimDBEntityFactory.build());

    // When
    Page<ClaimDBEntity> searchResult =
        repository.findByFilter(
            ClaimFilter.builder()
                .contractId(savedEntity.getContractId())
                .accountId(savedEntity.getAccountId())
                .offerId(savedEntity.getOfferId())
                .build(),
            PageRequest.of(0, 10));

    // Then
    assertEquals(1, searchResult.getTotalElements(), "searchResult size");
    ClaimDBEntity entity = searchResult.getContent().get(0);

    verifySameEntity(savedEntity, entity);
    verifyAuditFields(entity, CURRENT_AUDITOR);
  }

  @Test
  void claim_id_as_unique_constraint() {
    // Given
    var claim1 = claimDBEntityFactory.build();
    var claim2 = claimDBEntityFactory.build();
    // When
    repository.save(claim1);

    // Then
    try {
      repository.save(claim2);
    } catch (Exception e) {
      assertEquals(DbActionExecutionException.class, e.getClass());
    } finally {
      Optional<ClaimDBEntity> entityOpt = repository.findByClaimId(claim1.getClaimId());
      assertTrue(entityOpt.isPresent());
      assertEquals(claim1.getClaimId(), claim2.getClaimId());
    }
  }

  private void verifyAuditFields(AbstractAuditableEntity entity, String updater) {
    assertAll(
        "Audit properties",
        () -> assertNotNull(entity.getVersion(), "version"),
        () -> assertNotNull(entity.getCreatedAt(), "getCreatedAt()"),
        () -> assertEquals("SYSTEM", entity.getCreatedBy(), "getCreatedBy()"),
        () -> assertNotNull(entity.getUpdatedAt(), "getUpdatedAt()"),
        () -> assertEquals(updater, entity.getUpdatedBy(), "getUpdatedBy()"));
  }

  private void verifySameEntity(ClaimDBEntity expected, ClaimDBEntity entity) {
    assertAll(
        "Mapped properties",
        () -> assertNotNull(entity.getId(), "id"),
        () -> assertEquals(expected.getClaimId(), entity.getClaimId(), "claimId"),
        () -> assertEquals(expected.getEventDate(), entity.getEventDate(), "eventDate"),
        () ->
            assertEquals(
                expected.getNotificationDate(), entity.getNotificationDate(), "notificationDate"),
        () -> assertEquals(expected.getEntryDate(), entity.getEntryDate(), "entryDate"),
        () -> assertEquals(expected.getDescription(), entity.getDescription(), "description"),
        () -> assertEquals(expected.getPlaceOfEvent(), entity.getPlaceOfEvent(), "placeOfEvent"),
        () -> assertEquals(expected.getContractId(), entity.getContractId(), "contractId"),
        () -> assertEquals(expected.getOfferId(), entity.getOfferId(), "offerId"),
        () -> assertEquals(expected.getAccountId(), entity.getAccountId(), "accountId"),
        () -> assertEquals(expected.getType(), entity.getType(), "type"),
        () -> assertEquals(expected.getCoverageId(), entity.getCoverageId(), "coverageId"),
        () -> assertEquals(expected.getStatus(), entity.getStatus(), "status"),
        () -> assertEquals(expected.getProductId(), entity.getProductId(), "productId"),
        () -> assertEquals(expected.getInsuranceId(), entity.getInsuranceId(), "insuranceId"),
        () ->
            assertEquals(
                expected.getWithoutCoverage(), entity.getWithoutCoverage(), "withoutCoverage"),
        () -> assertEquals(expected.getExternalId(), entity.getExternalId(), "externalId"));
  }
}
