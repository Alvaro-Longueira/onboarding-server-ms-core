package com.wefox.onboarding.server.ms.core.application.port.input.interactor;

import com.wefox.onboarding.server.ms.core.application.port.input.UpdateClaimStatusUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.validator.UpdateClaimStatusValidator;
import com.wefox.onboarding.server.ms.core.application.service.ClaimService;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateClaimStatusImpl implements UpdateClaimStatusUseCase {

  private final ClaimService claimService;

  @Override
  @Transactional
  public Claim execute(InputValues input) {
    var claim = claimService.fetchClaim(input.getClaimId());
    UpdateClaimStatusValidator.validate(claim.getStatus(), input.getStatus());
    claim.setStatus(input.getStatus());
    return claimService.storeClaim(claim);
  }
}
