package com.wefox.onboarding.server.ms.core.application.port.input;

import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.server.lib.common.core.application.UseCase;
import lombok.Builder;
import lombok.Data;

public interface GetClaimByIdUseCase extends UseCase<GetClaimByIdUseCase.InputValues, Claim> {

  @Data
  @Builder
  class InputValues {

    private String claimId;
  }
}
