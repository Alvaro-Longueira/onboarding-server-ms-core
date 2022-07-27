package com.wefox.onboarding.server.ms.core.infrastructure.rest.api.contract;

import com.wefox.onboarding.server.ms.core.application.port.input.CreateClaimUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.GetClaimByIdUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.UpdateClaimStatusUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.UpdateClaimUseCase;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.domain.enums.ClaimStatus;
import com.wefox.onboarding.server.ms.core.domain.enums.ErrorDetailsEnum;
import com.wefox.server.lib.common.core.domain.pagination.Page;
import com.wefox.server.lib.common.core.exception.BusinessException;
import com.wefox.server.lib.common.core.exception.NotFoundException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MockBackend {

  List<String> existingClaims = Arrays.asList("10001", "10002", "10003");

  public Page<Claim> findAllClaimsUseCase() {
    return ClaimFactory.claimsPage(2);
  }

  public Claim getClaimByIdUseCase(GetClaimByIdUseCase.InputValues input) {
    String claimId = input.getClaimId();
    log.info("Get Claim by id {}", claimId);
    if (existingClaims.contains(claimId)) {
      log.info("claimId {} found!!", claimId);
      Claim claim = ClaimFactory.buildClaim();
      claim.setId(claimId);
      return claim;
    }
    throw new NotFoundException(ErrorDetailsEnum.CLAIM_NOT_FOUND);
  }

  public Claim createClaimUseCase(CreateClaimUseCase.InputValues inputValues) {
    if ("INVALID".equals(inputValues.getType())) {
      throw new BusinessException(ErrorDetailsEnum.COVERAGE_TYPE_NOT_COVERED);
    }
    if ("INVALID".equals(inputValues.getAccountId())) {
      throw new BusinessException(ErrorDetailsEnum.ACCOUNT_NOT_FOUND);
    }
    return ClaimFactory.buildClaim(inputValues);
  }

  public Claim updateClaimStatusUseCase(UpdateClaimStatusUseCase.InputValues input) {
    String claimId = input.getClaimId();
    if (existingClaims.contains(claimId)) {
      if (input.getStatus() == ClaimStatus.REJECTED) {
        throw new BusinessException(ErrorDetailsEnum.UPDATE_STATUS_VALIDATION);
      }
      Claim claim = ClaimFactory.buildClaim();
      claim.setStatus(input.getStatus());
      return claim;
    } else {
      throw new NotFoundException(ErrorDetailsEnum.CLAIM_NOT_FOUND);
    }
  }

  public Claim updateClaimUseCase(UpdateClaimUseCase.InputValues input) {
    String claimId = input.getClaimId();
    if (existingClaims.contains(claimId)) {
      OffsetDateTime now = OffsetDateTime.now();
      if (input.getEventDate().isPresent() && now.isBefore(input.getEventDate().get())) {
        throw new BusinessException(ErrorDetailsEnum.UPDATE_CLAIM_VALIDATION);
      }
      return ClaimFactory.buildClaim();
    } else {
      throw new NotFoundException(ErrorDetailsEnum.CLAIM_NOT_FOUND);
    }
  }
}
