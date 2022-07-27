package com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.request;

import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.common.ClaimStatusDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "Status Update", description = "Status input data when updating.")
public class ClaimStatusUpdateRequest {

  @NotNull private ClaimStatusDTO status;
}
