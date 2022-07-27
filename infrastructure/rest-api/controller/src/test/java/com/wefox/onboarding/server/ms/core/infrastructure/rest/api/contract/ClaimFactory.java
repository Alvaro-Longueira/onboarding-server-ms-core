package com.wefox.onboarding.server.ms.core.infrastructure.rest.api.contract;

import com.wefox.onboarding.server.ms.core.application.port.input.CreateClaimUseCase;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.domain.enums.ClaimStatus;
import com.wefox.server.lib.common.core.domain.pagination.Page;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClaimFactory {

  public static Claim buildClaim() {
    Claim result = new Claim();
    result.setId(UUID.randomUUID().toString());
    result.setType("DEFAULT");
    return result;
  }

  public static Claim buildClaim(CreateClaimUseCase.InputValues inputValues) {
    Claim result = new Claim();
    result.setId(inputValues.getClaimId());
    result.setEventDate(inputValues.getEventDate());
    result.setNotificationDate(inputValues.getNotificationDate());
    result.setEntryDate(OffsetDateTime.now());
    result.setDescription(inputValues.getDescription());
    result.setPlaceOfEvent(inputValues.getPlaceOfEvent());
    result.setContractId(inputValues.getContractId());
    result.setOfferId(inputValues.getOfferId());
    result.setAccountId(inputValues.getAccountId());
    result.setType(inputValues.getType());
    result.setStatus(ClaimStatus.CREATED);
    result.setProductId(inputValues.getProductId());
    result.setWithoutCoverage(inputValues.isWithoutCoverage());
    result.setType("DEFAULT");
    return result;
  }

  public static List<Claim> claimsList(int pageSize) {
    return Stream.iterate(0, i -> i)
        .limit(pageSize)
        .map(o -> buildClaim())
        .collect(Collectors.toList());
  }

  public static Page<Claim> claimsPage(int pageSize) {
    Page<Claim> result = new Page<>();
    result.setPageSize(pageSize);
    result.setContent(claimsList(pageSize));
    return result;
  }
}
