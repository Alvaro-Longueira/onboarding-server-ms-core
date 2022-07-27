package com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wefox.onboarding.server.ms.core.application.port.input.ClaimFilter;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.domain.enums.ClaimStatus;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.entity.ClaimDBEntity;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.mapper.DBClaimMapper;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.mapper.DBClaimMapperImpl;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.util.ClaimDBEntityFactory;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.util.ClaimFactory;
import com.wefox.server.lib.common.core.domain.pagination.Pageable;
import com.wefox.server.lib.common.data.core.mapper.SpringPageMapper;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class DBClaimRepositoryTests {

  private final ClaimFactory claimFactory = new ClaimFactory();
  private final ClaimDBEntityFactory claimDBEntityFactory = new ClaimDBEntityFactory();
  @Captor ArgumentCaptor<ClaimDBEntity> claimDBEntityCaptor;
  @Captor ArgumentCaptor<String> claimIdCaptor;
  @Mock private SpringDataClaimRepository springDataRepository;
  @Spy private DBClaimMapper mapper = new DBClaimMapperImpl();
  @Spy private SpringPageMapper pageMapper = new SpringPageMapper();
  @InjectMocks private DBClaimRepository repository;

  @Test
  void Given_a_claim_When_saved_Then_captured_same_claim_in_springDataRepository() {
    // Given
    final Random random = new Random();
    final Long id = random.nextLong();
    Claim claim = claimFactory.build();
    when(springDataRepository.save(any()))
        .thenAnswer(
            o -> {
              ClaimDBEntity arg = (ClaimDBEntity) o.getArguments()[0];
              arg.setId(id);
              return arg;
            });

    // When
    Claim savedEntity = repository.save(claim);

    // Then
    verify(springDataRepository, times(1)).save(claimDBEntityCaptor.capture());
    verifySameClaim(claim, claimDBEntityCaptor.getValue());
    verifySameClaim(claim, savedEntity);
  }

  @Test
  void Given_a_claim_When_update_Then_captured_same_claim_in_springDataRepository() {
    // Given
    OffsetDateTime initialDate = OffsetDateTime.now().minusDays(20L);
    final Random random = new Random();
    final Long id = random.nextLong();
    Claim claim = claimFactory.build();
    OffsetDateTime targetDate = OffsetDateTime.now().minusDays(2L);
    claim.setType("targetType");
    claim.setNotificationDate(targetDate);
    claim.setEventDate(Optional.of(targetDate));
    claim.setStatus(ClaimStatus.UNDER_REVIEW);
    // When
    repository.save(claim);

    // Then
    verify(springDataRepository, times(1)).save(claimDBEntityCaptor.capture());
    assertEquals(claim.getType(), claimDBEntityCaptor.getValue().getType());
    assertEquals(claim.getNotificationDate(), claimDBEntityCaptor.getValue().getNotificationDate());
    assertEquals(claim.getEventDate().get(), claimDBEntityCaptor.getValue().getEventDate());
    assertEquals(
        claim.getStatus().toString(), claimDBEntityCaptor.getValue().getStatus().toString());
  }

  @Test
  void Given_a_claimId_When_findByClaimId_existing_claim_Then_return_the_claim() {
    // Given
    final Random random = new Random();
    final Long id = random.nextLong();
    final String claimId = UUID.randomUUID().toString();

    final ClaimDBEntity claim = claimDBEntityFactory.build();
    claim.setId(id);
    claim.setClaimId(claimId);
    when(springDataRepository.findByClaimId(any())).thenAnswer(o -> Optional.of(claim));

    // When
    Optional<Claim> savedEntity = repository.findByClaimId(claimId);

    // Then
    assertTrue(savedEntity.isPresent(), "return entity");
    verify(springDataRepository, times(1)).findByClaimId(claimIdCaptor.capture());
    assertEquals(claimId, claimIdCaptor.getValue());
    verifySameClaim(savedEntity.get(), claim);
  }

  @Test
  void Given_a_claimId_When_findByClaimId_none_existing_claim_Then_empty_Optional() {
    // Given
    final Random random = new Random();
    final String claimId = UUID.randomUUID().toString();

    when(springDataRepository.findByClaimId(any())).thenAnswer(o -> Optional.empty());

    // When
    Optional<Claim> savedEntity = repository.findByClaimId(claimId);

    // Then
    assertFalse(savedEntity.isPresent(), "return entity");
    verify(springDataRepository, times(1)).findByClaimId(claimIdCaptor.capture());
    assertEquals(claimId, claimIdCaptor.getValue());
  }

  @Test
  void givenMultipleClaimsInDatabase_whenFindByFilter_thenReceivePageOfClaims() {
    Page<ClaimDBEntity> page =
        new PageImpl<>(
            List.of(
                claimDBEntityFactory.build(),
                claimDBEntityFactory.build(),
                claimDBEntityFactory.build()));

    when(springDataRepository.findByFilter(
            any(ClaimFilter.class),
            eq(PageRequest.of(Pageable.DEFAULT.getPageNumber(), Pageable.DEFAULT.getPageSize()))))
        .thenReturn(page);

    var filter = ClaimFilter.builder().accountId("ACT_11111111").build();

    var page1 = repository.findByFilter(filter, Pageable.DEFAULT);

    assertEquals(3, page1.getPageSize(), "page size");
    assertEquals(0, page1.getPageNumber(), "page number");
    assertEquals(3, page1.getTotalElements(), "total elements");
  }

  private void verifySameClaim(Claim domain, ClaimDBEntity entity) {
    assertAll(
        "Mapped properties",
        () -> assertNotNull(entity.getId(), "entity.id"),
        () -> assertEquals(domain.getId(), entity.getClaimId(), "claimId"),
        () -> assertEquals(domain.getEventDate().orElse(null), entity.getEventDate(), "eventDate"),
        () ->
            assertEquals(
                domain.getNotificationDate(), entity.getNotificationDate(), "notificationDate"),
        () -> assertNotNull(entity.getEntryDate(), "entryDate"),
        () ->
            assertEquals(
                domain.getDescription().orElse(null), entity.getDescription(), "description"),
        () ->
            assertEquals(
                domain.getPlaceOfEvent().orElse(null), entity.getPlaceOfEvent(), "placeOfEvent"),
        () ->
            assertEquals(domain.getContractId().orElse(null), entity.getContractId(), "contractId"),
        () -> assertEquals(domain.getOfferId().orElse(null), entity.getOfferId(), "offerId"),
        () -> assertEquals(domain.getAccountId(), entity.getAccountId(), "accountId"),
        () -> assertEquals(domain.getType(), entity.getType(), "type"),
        () ->
            assertEquals(domain.getCoverageId().orElse(null), entity.getCoverageId(), "coverageId"),
        () -> assertEquals(domain.getStatus().toString(), entity.getStatus().toString(), "status"),
        () -> assertEquals(domain.getProductId().orElse(null), entity.getProductId(), "productId"),
        () ->
            assertEquals(
                domain.getInsuranceId().orElse(null), entity.getInsuranceId(), "insuranceId"),
        () ->
            assertEquals(
                domain.getWithoutCoverage(), entity.getWithoutCoverage(), "withoutCoverage"));
  }

  private void verifySameClaim(Claim domain, Claim saved) {
    assertAll(
        "Mapped properties",
        () -> assertNotNull(saved.getId(), "entity.id"),
        () -> assertNull(domain.getInternalId(), "domain.internalId"),
        () -> assertEquals(domain.getId(), saved.getId(), "claimId"),
        () -> assertEquals(domain.getEventDate(), saved.getEventDate(), "eventDate"),
        () ->
            assertEquals(
                domain.getNotificationDate(), saved.getNotificationDate(), "notificationDate"),
        () -> assertNotNull(saved.getEntryDate(), "entryDate"),
        () -> assertEquals(domain.getDescription(), saved.getDescription(), "description"),
        () -> assertEquals(domain.getPlaceOfEvent(), saved.getPlaceOfEvent(), "placeOfEvent"),
        () -> assertEquals(domain.getContractId(), saved.getContractId(), "contractId"),
        () -> assertEquals(domain.getOfferId(), saved.getOfferId(), "offerId"),
        () -> assertEquals(domain.getAccountId(), saved.getAccountId(), "accountId"),
        () -> assertEquals(domain.getType(), saved.getType(), "type"),
        () -> assertEquals(domain.getCoverageId(), saved.getCoverageId(), "coverageId"),
        () -> assertEquals(domain.getStatus(), saved.getStatus(), "status"),
        () -> assertEquals(domain.getProductId(), saved.getProductId(), "productId"),
        () -> assertEquals(domain.getInsuranceId(), saved.getInsuranceId(), "insuranceId"),
        () ->
            assertEquals(
                domain.getWithoutCoverage(), saved.getWithoutCoverage(), "withoutCoverage"));
  }
}
