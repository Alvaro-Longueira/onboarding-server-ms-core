package com.wefox.onboarding.server.ms.core.domain.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Schema(
    description =
        "The status of the claim. Possible status values are: CREATED, PAID_OUT, REJECTED, UNDER_REVIEW, WITHDRAWN, ON_ERROR.")
public enum ClaimStatus {
  CREATED,
  UNDER_REVIEW,
  PAID_OUT,
  REJECTED,
  WITHDRAWN,
  ON_ERROR
}
