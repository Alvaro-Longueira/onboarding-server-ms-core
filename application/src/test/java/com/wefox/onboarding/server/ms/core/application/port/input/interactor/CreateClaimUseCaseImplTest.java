package com.wefox.onboarding.server.ms.core.application.port.input.interactor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.wefox.onboarding.server.ms.core.application.port.input.mapper.CreateClaimMapper;
import com.wefox.onboarding.server.ms.core.application.port.output.AccountService;
import com.wefox.onboarding.server.ms.core.application.port.output.ClaimChangedEventPublisher;
import com.wefox.onboarding.server.ms.core.application.port.output.ClaimRepository;
import com.wefox.onboarding.server.ms.core.application.port.output.ClaimTypeSettingsRepository;
import com.wefox.onboarding.server.ms.core.application.port.output.ContractService;
import com.wefox.onboarding.server.ms.core.application.util.ApplicationUtils;
import com.wefox.onboarding.server.ms.core.application.util.ClaimExample;
import com.wefox.onboarding.server.ms.core.application.util.ClaimFactory;
import com.wefox.onboarding.server.ms.core.application.util.CreateClaimInputValuesFactory;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.domain.entity.ClaimTypeSetting;
import com.wefox.onboarding.server.ms.core.domain.entity.Insurance;
import com.wefox.onboarding.server.ms.core.domain.enums.ClaimStatus;
import com.wefox.onboarding.server.ms.core.domain.enums.ErrorDetailsEnum;
import com.wefox.server.lib.common.core.exception.BusinessException;
import java.util.Optional;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CreateClaimUseCaseImplTest {

  private static final Insurance INSURANCE =
      new Insurance("ID_1", "PRODUCT_ID_1", "PRODUCT_DESCRIPTION");
  private final CreateClaimInputValuesFactory createClaimInputValuesFactory =
      new CreateClaimInputValuesFactory();
  private final ClaimFactory claimFactory = new ClaimFactory();

  @Mock private AccountService accountService;
  @Mock private ContractService contractService;
  @Mock private ClaimRepository claimRepository;
  @Mock private ClaimTypeSettingsRepository claimTypeSettingsRepository;
  @Mock private ClaimChangedEventPublisher eventPublisher;
  @Mock private CreateClaimMapper createClaimMapper;
  @Mock private ApplicationUtils utils;

  @InjectMocks private CreateClaimUseCaseImpl testUseCase;

  private Claim claim;

  @Test
  void successfulClaimCreation_contract() {
    givenAccountExists();
    andMapClaim(ClaimExample.CONTRACT_ID, c -> {});
    givenClaimTypeSettings();
    givenRepositoryReturnClaimBuilt();
    givenContractExists();

    var testInput = createClaimInputValuesFactory.build();

    testUseCase.execute(testInput);

    assertAccountIsCalled();
    assertClaimIsMapped();
    assertContractIsCalled();
    assertClaimIsSaved();
    assertClaimIPublished();
  }

  @Test
  void successfulClaimCreation_offer() {
    givenAccountExists();
    andMapClaim(ClaimExample.OFFER_ID, c -> {});
    givenRepositoryReturnClaimBuilt();

    var testInput = createClaimInputValuesFactory.build(ClaimExample.OFFER_ID);

    testUseCase.execute(testInput);

    assertAccountIsCalled();
    assertClaimIsMapped();
    assertClaimIsSaved();
    assertClaimIPublished();
    verifyNoInteractions(contractService);
  }

  @Test
  void successfulClaimCreation_ClaimType_4Digit_Length() {
    givenAccountExists();
    andMapClaim(ClaimExample.OFFER_ID, c -> c.setType("0071"));
    givenRepositoryReturnClaimBuilt();

    var testInput = createClaimInputValuesFactory.build(ClaimExample.OFFER_ID);

    testUseCase.execute(testInput);

    var transformedClaimType = "007100";
    assertEquals(transformedClaimType, claim.getType());

    assertAccountIsCalled();
    assertClaimIsMapped();
    assertClaimIsSaved();
    assertClaimIPublished();
    verifyNoInteractions(contractService);
  }

  @Test
  void unsuccessfulClaimCreation_invalidClaimTypeLength() {
    var testInput = createClaimInputValuesFactory.build(ClaimExample.OFFER_ID);
    andMapClaim(ClaimExample.OFFER_ID, c -> c.setType("007"));

    Executable executable = () -> testUseCase.execute(testInput);
    BusinessException exception = assertThrows(BusinessException.class, executable);

    var expectedCode = "CLAIM_DOM_005";
    var expectedInvalidParam = "Claim type: 007 has invalid length.";

    assertEquals(expectedCode, exception.getCode());
    assertEquals(expectedInvalidParam, exception.getInvalidParams().get("type"));

    assertClaimIsMapped();
    verifyNoInteractions(accountService);
    verifyNoInteractions(contractService);
    verifyNoInteractions(claimRepository);
  }

  @Test
  void unsuccessfulClaimCreation_invalidClaimTypeValue() {
    var testInput = createClaimInputValuesFactory.build(ClaimExample.OFFER_ID);
    andMapClaim(ClaimExample.OFFER_ID, c -> c.setType("abcd"));

    Executable executable = () -> testUseCase.execute(testInput);
    BusinessException exception = assertThrows(BusinessException.class, executable);

    var expectedCode = ErrorDetailsEnum.CREATE_CLAIM_VALIDATION.getCode();
    var expectedInvalidParam = "Claim type: abcd must contain numbers only.";

    assertEquals(expectedCode, exception.getCode());
    assertEquals(expectedInvalidParam, exception.getInvalidParams().get("type"));

    assertClaimIsMapped();
    verifyNoInteractions(accountService);
    verifyNoInteractions(contractService);
    verifyNoInteractions(claimRepository);
  }

  @Test
  void unsuccessfulClaimCreation_accountExistsFailure() {
    givenAccountDoesNotExist();
    givenRepositoryReturnClaim();
    andMapClaim(ClaimExample.CONTRACT_ID, c -> {});
    var testInput = createClaimInputValuesFactory.build();

    Executable executable = () -> testUseCase.execute(testInput);
    BusinessException exception = assertThrows(BusinessException.class, executable);

    var expectedCode = ErrorDetailsEnum.ACCOUNT_NOT_FOUND.getCode();
    assertEquals(expectedCode, exception.getCode());

    assertEquals(ClaimStatus.ON_ERROR, claim.getStatus());
    assertClaimIsMapped();
    assertAccountIsCalled();
    assertClaimIsSaved();
    verifyNoInteractions(contractService);
  }

  @Test
  void unsuccessfulClaimCreation_withoutCoverageBadInputValues() {
    var testInput = createClaimInputValuesFactory.build(ClaimExample.INVALID_DOM_005);
    andMapClaim(ClaimExample.INVALID_DOM_005, c -> {});

    Executable executable = () -> testUseCase.execute(testInput);
    BusinessException exception = assertThrows(BusinessException.class, executable);

    var expectedCode = ErrorDetailsEnum.CREATE_CLAIM_VALIDATION.getCode();
    assertEquals(expectedCode, exception.getCode());

    assertClaimIsMapped();
    verifyNoInteractions(accountService);
    verifyNoInteractions(contractService);
    verifyNoInteractions(claimRepository);
  }

  @Test
  void successfulClaimCreation_skipSymassValidation() {
    when(utils.skipSymass()).thenReturn(true);
    andMapClaim(ClaimExample.CONTRACT_ID, c -> {});
    givenRepositoryReturnClaim();

    var testInput = createClaimInputValuesFactory.build();

    testUseCase.execute(testInput);

    verifyNoInteractions(accountService);
    verifyNoInteractions(contractService);
    assertClaimIsMapped();
    assertClaimIsSaved();
    assertClaimIPublished();
  }

  @Test
  void unsuccessfulClaimCreation_typeSettingNotFound() {
    givenAccountExists();
    andMapClaim(ClaimExample.CONTRACT_ID, c -> {});
    givenRepositoryReturnClaim();
    givenClaimTypeSettingsReturnOptional();

    var testInput = createClaimInputValuesFactory.build();

    Executable executable = () -> testUseCase.execute(testInput);

    var exception = assertThrows(BusinessException.class, executable);
    var expectedCode = ErrorDetailsEnum.UNKNOWN_CLAIM_TYPE.getCode();
    assertEquals(expectedCode, exception.getCode());

    assertEquals(ClaimStatus.ON_ERROR, claim.getStatus());
    assertAccountIsCalled();
    assertClaimIsMapped();
    assertClaimTypeSettingsRepositoryIsCalled();
    verifyNoInteractions(contractService);
    verifyNoInteractions(eventPublisher);
  }

  @Test
  void successfulClaimCreation_noPublishBecauseOfNullEventDate() {
    givenAccountExists();
    andMapClaim(ClaimExample.CONTRACT_ID, c -> c.setEventDate(Optional.empty()));
    givenRepositoryReturnClaimBuilt();

    var testInput = createClaimInputValuesFactory.build();
    testInput.setEventDate(Optional.empty());

    testUseCase.execute(testInput);

    assertAccountIsCalled();
    assertClaimIsMapped();
    assertClaimIsSaved();
    verifyNoInteractions(claimTypeSettingsRepository);
    verifyNoInteractions(contractService);
    verifyNoInteractions(eventPublisher);
  }

  private void givenAccountExists() {
    when(accountService.accountExists(any())).thenReturn(true);
  }

  private void givenAccountDoesNotExist() {
    when(accountService.accountExists(any())).thenReturn(false);
  }

  private void givenContractExists() {
    when(contractService.checkCoverage(any(), any())).thenReturn(Optional.of(INSURANCE));
  }

  private void givenClaimTypeSettings() {
    when(claimTypeSettingsRepository.findCoverageType(any()))
        .thenReturn(Optional.of(new ClaimTypeSetting()));
  }

  private void givenClaimTypeSettingsReturnOptional() {
    when(claimTypeSettingsRepository.findCoverageType(any())).thenReturn(Optional.empty());
  }

  private void givenRepositoryReturnClaim() {
    when(claimRepository.save(any())).thenReturn(claimFactory.build());
  }

  private void givenRepositoryReturnClaimBuilt() {
    when(claimRepository.save(any())).thenReturn(claim);
  }

  private void andMapClaim(ClaimExample example, Consumer<Claim> consumer) {
    claim = claimFactory.build(example, consumer);
    when(createClaimMapper.toDomainEntity(any())).thenReturn(claim);
  }

  private void assertClaimIPublished() {
    verify(eventPublisher, times(1)).execute(any());
  }

  private void assertClaimIsSaved() {
    verify(claimRepository, times(1)).save(any());
  }

  private void assertClaimTypeSettingsRepositoryIsCalled() {
    verify(claimTypeSettingsRepository, times(1)).findCoverageType(any());
  }

  private void assertAccountIsCalled() {
    verify(accountService, times(1)).accountExists(any());
  }

  private void assertContractIsCalled() {
    verify(contractService, times(1)).checkCoverage(any(), any());
  }

  private void assertClaimIsMapped() {
    verify(createClaimMapper, times(1)).toDomainEntity(any());
  }
}
