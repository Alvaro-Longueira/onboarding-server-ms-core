package com.wefox.onboarding.server.ms.core.application.port.input.interactor;

import com.wefox.onboarding.server.ms.core.application.port.input.GetClaimByIdUseCase;
import com.wefox.onboarding.server.ms.core.application.service.ClaimService;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetClaimByIdImpl implements GetClaimByIdUseCase {

  private final ClaimService claimService;

  @Override
  public Claim execute(InputValues input) {
    return claimService.fetchClaim(input.getClaimId());
  }
}
