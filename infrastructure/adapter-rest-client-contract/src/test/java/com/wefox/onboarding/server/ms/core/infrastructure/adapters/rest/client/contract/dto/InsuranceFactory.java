package com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.dto;

import com.wefox.onboarding.server.ms.core.domain.entity.Insurance;
import com.wefox.server.lib.common.test.factory.AbstractFactory;

public class InsuranceFactory extends AbstractFactory<Insurance> {

  public InsuranceFactory() {
    super(Insurance.class, "test-data/");
  }

  public Insurance build() {
    return super.load("Insurance.json");
  }
}
