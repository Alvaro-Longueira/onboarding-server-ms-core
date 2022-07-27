package com.wefox.onboarding.server.ms.core.application.port.input.interactor;

import com.wefox.onboarding.server.ms.core.application.port.input.FindAllClaimsUseCase;
import com.wefox.onboarding.server.ms.core.application.port.output.ClaimRepository;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.server.lib.common.core.domain.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindAllClaimsImpl implements FindAllClaimsUseCase {

  private final ClaimRepository claimRepository;

  @Override
  public Page<Claim> execute(InputValues input) {
    return claimRepository.findByFilter(input.getFilter(), input.getPageable());
  }
}
