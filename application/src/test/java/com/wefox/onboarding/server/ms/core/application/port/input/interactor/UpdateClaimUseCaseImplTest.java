package com.wefox.onboarding.server.ms.core.application.port.input.interactor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.wefox.onboarding.server.ms.core.application.port.input.UpdateClaimUseCase.InputValues;
import com.wefox.onboarding.server.ms.core.application.port.input.mapper.UpdateClaimMapper;
import com.wefox.onboarding.server.ms.core.application.port.output.ClaimChangedEventPublisher;
import com.wefox.onboarding.server.ms.core.application.port.output.ClaimRepository;
import com.wefox.onboarding.server.ms.core.application.port.output.ClaimTypeSettingsRepository;
import com.wefox.onboarding.server.ms.core.application.port.output.ContractService;
import com.wefox.onboarding.server.ms.core.application.util.ApplicationUtils;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.domain.entity.ClaimTypeSetting;
import com.wefox.onboarding.server.ms.core.domain.enums.ErrorDetailsEnum;
import com.wefox.server.lib.common.core.exception.NotFoundException;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateClaimUseCaseImplTest {

  private final Optional<OffsetDateTime> originalEventDate =
      Optional.of(OffsetDateTime.now().minusDays(20L));
  private final Optional<OffsetDateTime> targetEventDate =
      Optional.of(OffsetDateTime.now().minusDays(5L));
  private final OffsetDateTime originalNotificationDate = OffsetDateTime.now().minusDays(10L);
  private final OffsetDateTime targetNotificationDate = OffsetDateTime.now().minusDays(1L);
  private final Optional<String> contractId = Optional.of("contractId");
  private final String originalClaimType = "007100";
  private final String targetClaimType = "007001";

  @Captor ArgumentCaptor<Claim> claimCaptor;
  @Mock private ContractService contractService;
  @Mock private ClaimRepository claimRepository;
  @Mock private ClaimTypeSettingsRepository claimTypeSettingsRepository;
  @Mock private ClaimChangedEventPublisher eventPublisher;
  @Mock private UpdateClaimMapper updateClaimMapper;
  @Mock private ApplicationUtils utils;
  @InjectMocks private UpdateClaimUseCaseImpl testUseCase;
  private Claim claim;

  @Test
  void notFoundClaim() {
    givenClaimNotFound();

    var inputValues = defaultInputValues(RandomUtils.nextBoolean()).build();

    Executable executable = () -> testUseCase.execute(inputValues);
    var exception = assertThrows(NotFoundException.class, executable);
    var expectedCode = "CLAIM_DOM_003";
    assertEquals(expectedCode, exception.getCode());

    assertClaimIsSearched();
    assertCoverageCheckIsNotCalled();
    assertClaimIsNotSaved();
  }

  @Test
  void successfulClaimUpdateWithoutCoverage() {
    givenValidClaimIdWithoutCoverage();
    givenClaimIsMerged();
    givenRepositoryReturnClaim();

    var inputValues = defaultInputValues(Boolean.TRUE).build();
    testUseCase.execute(inputValues);

    assertClaimIsSearched();
    assertCoverageCheckIsNotCalled();
    assertDefaultClaimIsUpdated();
  }

  @Test
  void successfulClaimUpdateWithoutCoverageFalse() {
    givenValidClaimIdWithoutCoverageFalse();
    givenClaimIsMerged();
    givenClaimTypeSettings();
    givenRepositoryReturnClaim();

    var inputValues = defaultInputValues(Boolean.FALSE).build();
    testUseCase.execute(inputValues);

    assertClaimIsSearched();
    assertCoverageCheckIsCalled();
    assertDefaultClaimIsUpdated();
  }

  private void givenClaimNotFound() {
    when(claimRepository.findByClaimId(any()))
        .thenThrow(new NotFoundException(ErrorDetailsEnum.CLAIM_NOT_FOUND));
  }

  @Test
  void successfulClaimUpdateWithDescriptionAndPlaceOfEvent() {
    givenValidClaimIdWithoutCoverageFalse();
    givenClaimIsMerged();
    givenClaimTypeSettings();
    givenRepositoryReturnClaim();

    var inputValues = allInputValues(Boolean.FALSE).build();
    testUseCase.execute(inputValues);

    assertClaimIsSearched();
    assertCoverageCheckIsCalled();
    assertCompleteClaimIsUpdated();
  }

  private void givenValidClaimIdWithoutCoverage() {
    buildMergedClaim(Boolean.TRUE);
    when(claimRepository.findByClaimId(any())).thenReturn(Optional.of(claim));
  }

  private void givenValidClaimIdWithoutCoverageFalse() {
    buildMergedClaim(Boolean.FALSE);
    when(claimRepository.findByClaimId(any())).thenReturn(Optional.of(claim));
  }

  private void givenRepositoryReturnClaim() {
    when(claimRepository.save(any())).thenReturn(claim);
  }

  private void givenClaimTypeSettings() {
    when(claimTypeSettingsRepository.findCoverageType(any()))
        .thenReturn(Optional.of(new ClaimTypeSetting()));
  }

  private void givenClaimIsMerged() {
    when(updateClaimMapper.merge(any(), any())).thenReturn(claim);
  }

  private void assertClaimIsSearched() {
    verify(claimRepository, times(1)).findByClaimId(any());
  }

  private void assertDefaultClaimIsUpdated() {
    verify(claimRepository, times(1)).save(claimCaptor.capture());
    assertEquals(claim.getType(), claimCaptor.getValue().getType());
    assertEquals(claim.getEventDate(), claimCaptor.getValue().getEventDate());
    assertEquals(claim.getNotificationDate(), claimCaptor.getValue().getNotificationDate());
  }

  private void assertCompleteClaimIsUpdated() {
    verify(claimRepository, times(1)).save(claimCaptor.capture());
    assertEquals(claim.getType(), claimCaptor.getValue().getType());
    assertEquals(claim.getEventDate(), claimCaptor.getValue().getEventDate());
    assertEquals(claim.getNotificationDate(), claimCaptor.getValue().getNotificationDate());
    assertEquals(claim.getDescription(), claimCaptor.getValue().getDescription());
    assertEquals(claim.getPlaceOfEvent(), claimCaptor.getValue().getPlaceOfEvent());
  }

  private void assertCoverageCheckIsCalled() {
    verify(contractService, times(1)).checkCoverage(claimCaptor.capture(), any());
    assertEquals(claim.getContractId(), claimCaptor.getValue().getContractId());
    assertEquals(claim.getEventDate(), claimCaptor.getValue().getEventDate());
    assertEquals(claim.getType(), claimCaptor.getValue().getType());
    assertEquals(claim.getWithoutCoverage(), claimCaptor.getValue().getWithoutCoverage());
  }

  private void assertCoverageCheckIsNotCalled() {
    verifyNoInteractions(contractService);
  }

  private void assertClaimIsNotSaved() {
    verify(claimRepository, times(0)).save(any());
  }

  private InputValues.InputValuesBuilder defaultInputValues(Boolean withoutCoverage) {
    return InputValues.builder()
        .claimId("claimId")
        .eventDate(targetEventDate)
        .notificationDate(targetNotificationDate)
        .type(targetClaimType)
        .withoutCoverage(withoutCoverage);
  }

  private InputValues.InputValuesBuilder allInputValues(Boolean withoutCoverage) {
    return defaultInputValues(withoutCoverage)
        .description(Optional.of("Random description"))
        .placeOfEvent(Optional.of("Tokyo"));
  }

  private void buildMergedClaim(Boolean withoutCoverage) {
    claim = new Claim();
    claim.setContractId(contractId);
    claim.setEventDate(originalEventDate);
    claim.setNotificationDate(originalNotificationDate);
    claim.setType(originalClaimType);
    claim.setWithoutCoverage(withoutCoverage);
  }
}
