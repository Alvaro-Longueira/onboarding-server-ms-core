package com.wefox.onboarding.server.ms.core.application.port.input.interactor;

import com.wefox.onboarding.server.ms.core.application.port.input.UpdateClaimUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.mapper.UpdateClaimMapper;
import com.wefox.onboarding.server.ms.core.application.port.input.validator.ClaimValidator;
import com.wefox.onboarding.server.ms.core.application.port.output.ClaimChangedEventPublisher;
import com.wefox.onboarding.server.ms.core.application.port.output.ClaimRepository;
import com.wefox.onboarding.server.ms.core.application.port.output.ClaimTypeSettingsRepository;
import com.wefox.onboarding.server.ms.core.application.port.output.ContractService;
import com.wefox.onboarding.server.ms.core.application.util.ApplicationUtils;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.domain.entity.ClaimTypeSetting;
import com.wefox.onboarding.server.ms.core.domain.enums.ErrorDetailsEnum;
import com.wefox.server.lib.common.core.exception.BusinessException;
import com.wefox.server.lib.common.core.exception.NotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateClaimUseCaseImpl implements UpdateClaimUseCase {

  private final ContractService contractService;
  private final ClaimRepository claimRepository;
  private final ClaimTypeSettingsRepository claimTypeSettingsRepository;
  private final ClaimChangedEventPublisher eventPublisher;
  private final UpdateClaimMapper updateClaimMapper;
  private final ApplicationUtils utils;

  @Override
  @Transactional
  public Claim execute(InputValues inputValues) {
    Claim oldClaim = findClaim(inputValues.getClaimId());
    Claim claimToUpdate = updateClaimMapper.merge(oldClaim, inputValues);
    ClaimValidator.validateUpdate(claimToUpdate);

    if (!utils.skipSymass()) {
      checkCoverage(claimToUpdate);
    }

    Claim updatedClaim = claimRepository.save(claimToUpdate);

    publishClaim(updatedClaim);
    return updatedClaim;
  }

  public Claim findClaim(String claimId) {
    return claimRepository
        .findByClaimId(claimId)
        .orElseThrow(() -> new NotFoundException(ErrorDetailsEnum.CLAIM_NOT_FOUND));
  }

  private void checkCoverage(Claim claim) {
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
              claim.setInsuranceId(Optional.ofNullable(insurance.id()));
              claim.setProductId(Optional.ofNullable(insurance.productId()));
              claim.setProductDescription(Optional.ofNullable(insurance.productDescription()));
            });
  }

  private ClaimTypeSetting getClaimTypeSetting(String claimType) {
    return claimTypeSettingsRepository
        .findCoverageType(claimType)
        .orElseThrow(() -> new BusinessException(ErrorDetailsEnum.UNKNOWN_CLAIM_TYPE));
  }

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
