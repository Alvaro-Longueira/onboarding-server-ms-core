package com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.response;

import com.wefox.server.lib.common.api.pagination.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ResourcePageClaim")
public class ResourcePageClaim extends PageDTO<ClaimResponse> {}
