package com.wefox.onboarding.server.ms.core.application.util;

import com.wefox.onboarding.server.ms.core.application.port.input.CreateClaimUseCase.InputValues;
import com.wefox.server.lib.common.test.factory.AbstractFactory;

public class CreateClaimInputValuesFactory extends AbstractFactory<InputValues> {

  public CreateClaimInputValuesFactory() {
    super(InputValues.class, "test-data/");
  }

  public InputValues build(ClaimExample example) {
    return super.load("CreateClaimInputValues_" + example.name() + ".json");
  }

  /** Builds a Claim using ClaimExample.CONTRACT_ID */
  public InputValues build() {
    return this.build(ClaimExample.CONTRACT_ID);
  }
}
