package com.wefox.onboarding.server.ms.core.application.service;

import com.wefox.onboarding.server.ms.core.application.port.output.ClaimChangedEventPublisher;
import com.wefox.onboarding.server.ms.core.application.port.output.ClaimRepository;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.domain.enums.ErrorDetailsEnum;
import com.wefox.server.lib.common.core.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClaimService {

  private final ClaimRepository claimRepository;
  private final ClaimChangedEventPublisher eventPublisher;

  public Claim fetchClaim(String claimId) {
    return claimRepository
        .findByClaimId(claimId)
        .orElseThrow(() -> new NotFoundException(ErrorDetailsEnum.CLAIM_NOT_FOUND));
  }

  public Claim storeClaim(Claim claim) {
    var saved = claimRepository.save(claim);
    publishClaim(saved);
    return saved;
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
