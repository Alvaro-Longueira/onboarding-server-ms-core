package com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.dto;

import com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.dto.contracts.limits.ContractLimitsDto;
import com.wefox.server.lib.common.test.factory.AbstractFactory;

public class ContractLimitsFactory extends AbstractFactory<ContractLimitsDto> {

  public ContractLimitsFactory() {
    super(ContractLimitsDto.class, "test-data/");
  }

  public ContractLimitsDto build() {
    return super.load("ContractLimits.json");
  }
}
