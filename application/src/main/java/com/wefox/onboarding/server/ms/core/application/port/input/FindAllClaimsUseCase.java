package com.wefox.onboarding.server.ms.core.application.port.input;

import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.server.lib.common.core.application.PagedQuery;
import com.wefox.server.lib.common.core.domain.pagination.Pageable;
import lombok.Builder;
import lombok.Data;

public interface FindAllClaimsUseCase extends PagedQuery<FindAllClaimsUseCase.InputValues, Claim> {

  @Data
  @Builder
  class InputValues {

    private ClaimFilter filter;
    private Pageable pageable;
  }
}
