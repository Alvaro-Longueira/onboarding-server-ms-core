package com.wefox.onboarding.server.ms.core.application.port.input;

import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.domain.enums.ClaimStatus;
import com.wefox.server.lib.common.core.application.UseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface UpdateClaimStatusUseCase
    extends UseCase<UpdateClaimStatusUseCase.InputValues, Claim> {

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  class InputValues {

    private String claimId;
    private ClaimStatus status;
  }
}
