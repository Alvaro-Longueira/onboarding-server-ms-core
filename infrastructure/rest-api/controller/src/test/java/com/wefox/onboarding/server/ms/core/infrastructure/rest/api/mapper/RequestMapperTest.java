package com.wefox.onboarding.server.ms.core.infrastructure.rest.api.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wefox.onboarding.server.ms.core.application.port.input.ClaimFilter;
import com.wefox.onboarding.server.ms.core.application.port.input.CreateClaimUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.FindAllClaimsUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.GetClaimByIdUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.UpdateClaimStatusUseCase;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.request.ClaimRequest;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.request.ClaimStatusUpdateRequest;
import com.wefox.server.lib.common.core.domain.pagination.Pageable;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;

class RequestMapperTest {

  private final EasyRandom generator = new EasyRandom();
  private final RequestMapper mapper = new RequestMapperImpl();

  @Test
  void toCreateInputValues__givenAnyClaimRequest_whenMap_thenReturnsCorrectData() {
    ClaimRequest input = generator.nextObject(ClaimRequest.class);

    CreateClaimUseCase.InputValues result = mapper.toCreateInputValues(input);

    assertAll(
        () -> assertEquals(input.getClaimId(), result.getClaimId()),
        () -> assertEquals(input.getEventDate(), result.getEventDate().get()),
        () -> assertEquals(input.getNotificationDate(), result.getNotificationDate()),
        () -> assertEquals(input.getContractId(), result.getContractId().get()),
        () -> assertEquals(input.getOfferId(), result.getOfferId().get()),
        () -> assertEquals(input.getAccountId(), result.getAccountId()),
        () -> assertEquals(input.getProductId(), result.getProductId().get()),
        () -> assertEquals(input.getWithoutCoverage(), result.isWithoutCoverage()),
        () -> assertEquals(input.getSymassId(), result.getSymassId().get()));
  }

  @Test
  void toGetInputValues__givenAnyClaimId_whenMap_thenReturnsCorrectData() {
    String input = generator.nextObject(String.class);

    GetClaimByIdUseCase.InputValues result = mapper.toGetInputValues(input);

    assertAll(() -> assertEquals(input, result.getClaimId()));
  }

  @Test
  void toFindAllClaimsInput__givenAnyClaimFilterAndPageable_whenMap_thenReturnsCorrectData() {
    ClaimFilter filter = generator.nextObject(ClaimFilter.class);
    Pageable pageable = generator.nextObject(Pageable.class);

    FindAllClaimsUseCase.InputValues result = mapper.toFindAllClaimsInput(filter, pageable);

    assertAll(
        () -> assertEquals(filter, result.getFilter()),
        () -> assertEquals(pageable, result.getPageable()));
  }

  @Test
  void toUpdateClaimStatusInput__givenAnyClaimIdAndStatus_whenMap_thenReturnsCorrectData() {
    String claimId = generator.nextObject(String.class);
    ClaimStatusUpdateRequest request = generator.nextObject(ClaimStatusUpdateRequest.class);

    UpdateClaimStatusUseCase.InputValues result = mapper.toUpdateClaimStatusInput(claimId, request);

    assertAll(
        () -> assertEquals(claimId, result.getClaimId()),
        () -> assertEquals(request.getStatus().name(), result.getStatus().name()));
  }
}
