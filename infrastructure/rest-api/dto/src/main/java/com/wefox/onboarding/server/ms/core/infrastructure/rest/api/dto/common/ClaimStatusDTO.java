package com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Schema(
    description =
        "The status of the claim. Possible status values are: "
            + "CREATED, PAID_OUT, REJECTED, UNDER_REVIEW, WITHDRAWN")
public enum ClaimStatusDTO {
  CREATED,
  UNDER_REVIEW,
  PAID_OUT,
  REJECTED,
  WITHDRAWN
}
