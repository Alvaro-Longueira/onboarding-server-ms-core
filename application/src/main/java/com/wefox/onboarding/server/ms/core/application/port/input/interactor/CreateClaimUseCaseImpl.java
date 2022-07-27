package com.wefox.onboarding.server.ms.core.application.port.input.interactor;

import com.wefox.onboarding.server.ms.core.application.port.input.CreateClaimUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.mapper.CreateClaimMapper;
import com.wefox.onboarding.server.ms.core.application.port.input.validator.ClaimValidator;
import com.wefox.onboarding.server.ms.core.application.port.output.AccountService;
import com.wefox.onboarding.server.ms.core.application.port.output.ClaimChangedEventPublisher;
import com.wefox.onboarding.server.ms.core.application.port.output.ClaimRepository;
import com.wefox.onboarding.server.ms.core.application.port.output.ClaimTypeSettingsRepository;
import com.wefox.onboarding.server.ms.core.application.port.output.ContractService;
import com.wefox.onboarding.server.ms.core.application.util.ApplicationUtils;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.domain.entity.ClaimTypeSetting;
import com.wefox.onboarding.server.ms.core.domain.enums.ClaimStatus;
import com.wefox.onboarding.server.ms.core.domain.enums.ErrorDetailsEnum;
import com.wefox.server.lib.common.core.exception.BusinessException;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateClaimUseCaseImpl implements CreateClaimUseCase {

  private final AccountService accountService;
  private final ContractService contractService;
  private final ClaimRepository claimRepository;
  private final ClaimTypeSettingsRepository claimTypeSettingsRepository;
  private final ClaimChangedEventPublisher eventPublisher;
  private final CreateClaimMapper createClaimMapper;
  private final ApplicationUtils utils;

  @Override
  public Claim execute(InputValues input) {
    Claim claimToCreate = createClaimMapper.toDomainEntity(input);
    ClaimValidator.validateCreation(claimToCreate);

    try {
      validateBusinessInformation(claimToCreate);
      Claim createdClaim = saveClaim(claimToCreate);
      publishClaim(createdClaim);
      return createdClaim;

    } catch (BusinessException exception) {
      // Temporal issue: Persist all claims, set internal error status and log error
      // https://financefox.atlassian.net/browse/CL-973
      log.error(
          "Some error with claim {} has occurred: {}",
          claimToCreate.getId(),
          exception.getMessage());

      claimToCreate.setStatus(ClaimStatus.ON_ERROR);
      saveClaim(claimToCreate);
      throw exception;
    }
  }

  @Transactional
  public Claim saveClaim(Claim claim) {
    claim.setEntryDate(OffsetDateTime.now());
    return claimRepository.save(claim);
  }

  @SuppressWarnings("CPD-START")
  private void validateBusinessInformation(Claim claim) {
    if (utils.skipSymass()) {
      return;
    }

    boolean exists = accountService.accountExists(claim.getAccountId());
    if (!exists) {
      throw new BusinessException(ErrorDetailsEnum.ACCOUNT_NOT_FOUND);
    }

    if (claim.getWithoutCoverage()) {
      return;
    }
    if (claim.getEventDate().isEmpty()) {
      return;
    }

    ClaimTypeSetting claimTypeSetting = getClaimTypeSetting(claim.getType());
    contractService
        .checkCoverage(claim, claimTypeSetting)
        .ifPresent(
            insurance -> {
              claim.setInsuranceId(Optional.of(insurance.id()));
              claim.setProductId(Optional.of(insurance.productId()));
              claim.setProductDescription(Optional.of(insurance.productDescription()));
            });
  }

  private ClaimTypeSetting getClaimTypeSetting(String claimType) {
    return claimTypeSettingsRepository
        .findCoverageType(claimType)
        .orElseThrow(() -> new BusinessException(ErrorDetailsEnum.UNKNOWN_CLAIM_TYPE));
  }

  @SuppressWarnings("CPD-END")
  private void publishClaim(Claim claim) {
    if (claim.getEventDate().isPresent()) {
      eventPublisher.execute(
          ClaimChangedEventPublisher.Arguments.<String, Claim>builder()
              .value(claim)
              .key(claim.getId())
              .build());
    }
  }
}
