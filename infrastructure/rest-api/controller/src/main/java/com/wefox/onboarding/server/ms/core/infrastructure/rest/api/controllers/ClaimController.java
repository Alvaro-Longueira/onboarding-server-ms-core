package com.wefox.onboarding.server.ms.core.infrastructure.rest.api.controllers;

import com.wefox.onboarding.server.ms.core.application.port.input.ClaimFilter;
import com.wefox.onboarding.server.ms.core.application.port.input.CreateClaimUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.FindAllClaimsUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.GetClaimByIdUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.UpdateClaimStatusUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.UpdateClaimUseCase;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.request.ClaimRequest;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.request.ClaimStatusUpdateRequest;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.request.ClaimUpdateRequest;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.response.ClaimResponse;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.mapper.RequestMapper;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.mapper.ResponseMapper;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.spec.ClaimApi;
import com.wefox.server.lib.common.api.pagination.PageDTO;
import com.wefox.server.lib.common.api.pagination.PageableDTO;
import com.wefox.server.lib.common.core.domain.pagination.Page;
import com.wefox.server.lib.common.core.domain.pagination.PageMapper;
import com.wefox.server.lib.common.core.domain.pagination.PageableMapper;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ClaimController implements ClaimApi {

  private final CreateClaimUseCase createClaimUseCase;
  private final FindAllClaimsUseCase findAllClaimsUseCase;
  private final GetClaimByIdUseCase getClaimByIdUseCase;
  private final UpdateClaimStatusUseCase updateClaimStatusUseCase;
  private final UpdateClaimUseCase updateClaimUseCase;

  private final RequestMapper requestMapper;
  private final ResponseMapper responseMapper;
  private final PageMapper pageMapper;
  private final PageableMapper pageableMapper;

  @Override
  public PageDTO<ClaimResponse> getClaims(
      String accountId, String contractIdd, String offerId, int pageNumber, int pageSize) {
    PageableDTO pageable = new PageableDTO();
    pageable.setPageNumber(pageNumber);
    pageable.setPageSize(pageSize);
    ClaimFilter filter = ClaimFilter.builder().build();
    filter.setAccountId(accountId);
    filter.setContractId(contractIdd);
    filter.setOfferId(offerId);

    final Page<Claim> result =
        findAllClaimsUseCase.execute(
            requestMapper.toFindAllClaimsInput(filter, pageableMapper.toDomainEntity(pageable)));
    Function<Claim, ClaimResponse> mapClaim = responseMapper::claimToClaimResponse;

    return pageMapper.toDTO(result, mapClaim);
  }

  @Override
  public ClaimResponse getClaimById(String claimId) {
    final Claim result = getClaimByIdUseCase.execute(requestMapper.toGetInputValues(claimId));
    return responseMapper.claimToClaimResponse(result);
  }

  @Override
  public ClaimResponse updateClaim(String claimId, ClaimUpdateRequest claimUpdateRequest) {
    return responseMapper.claimToClaimResponse(
        updateClaimUseCase.execute(requestMapper.toGetInputValues(claimId, claimUpdateRequest)));
  }

  @Override
  public ClaimResponse createClaim(ClaimRequest claimRequest) {
    return responseMapper.claimToClaimResponse(
        createClaimUseCase.execute(requestMapper.toCreateInputValues(claimRequest)));
  }

  @Override
  public ClaimResponse updateClaimStatus(
      String claimId, ClaimStatusUpdateRequest claimStatusUpdateRequest) {
    return responseMapper.claimToClaimResponse(
        updateClaimStatusUseCase.execute(
            requestMapper.toUpdateClaimStatusInput(claimId, claimStatusUpdateRequest)));
  }
}
