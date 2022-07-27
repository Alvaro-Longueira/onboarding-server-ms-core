package com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.dto.contracts.limits;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming
public record ContractLimitsDto(List<ContractLimitDto> contractLimits) {

  public ContractLimitsDto() {
    this(null);
  }
}
