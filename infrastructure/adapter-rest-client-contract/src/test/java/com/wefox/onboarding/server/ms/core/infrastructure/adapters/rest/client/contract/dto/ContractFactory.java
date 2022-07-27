package com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.dto;

import com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.dto.contracts.ContractDto;
import com.wefox.server.lib.common.test.factory.AbstractFactory;

public class ContractFactory extends AbstractFactory<ContractDto> {

  public ContractFactory() {
    super(ContractDto.class, "test-data/");
  }

  public ContractDto build() {
    return super.load("Contract.json");
  }
}
