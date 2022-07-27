package com.wefox.onboarding.server.ms.core.infrastructure.rest.api.mapper;

import com.wefox.onboarding.server.ms.core.application.port.input.ClaimFilter;
import com.wefox.onboarding.server.ms.core.application.port.input.CreateClaimUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.FindAllClaimsUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.GetClaimByIdUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.UpdateClaimStatusUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.UpdateClaimUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.mapper.OptionalMapper;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.request.ClaimRequest;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.request.ClaimStatusUpdateRequest;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.request.ClaimUpdateRequest;
import com.wefox.server.lib.common.core.domain.pagination.Pageable;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {OptionalMapper.class})
public interface RequestMapper {

  CreateClaimUseCase.InputValues toCreateInputValues(ClaimRequest claimRequest);

  GetClaimByIdUseCase.InputValues toGetInputValues(String claimId);

  FindAllClaimsUseCase.InputValues toFindAllClaimsInput(ClaimFilter filter, Pageable pageable);

  UpdateClaimUseCase.InputValues toGetInputValues(
      String claimId, ClaimUpdateRequest claimUpdateRequest);

  UpdateClaimStatusUseCase.InputValues toUpdateClaimStatusInput(
      String claimId, ClaimStatusUpdateRequest claimStatusUpdateRequest);

  default String mapEmptyString(String s) {
    return StringUtils.isNotBlank(s) ? s : null;
  }
}
