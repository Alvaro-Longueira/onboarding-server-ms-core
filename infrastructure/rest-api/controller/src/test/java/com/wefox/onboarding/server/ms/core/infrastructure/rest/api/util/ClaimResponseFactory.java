package com.wefox.onboarding.server.ms.core.infrastructure.rest.api.util;

import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.response.ClaimResponse;
import com.wefox.server.lib.common.test.factory.AbstractFactory;

public class ClaimResponseFactory extends AbstractFactory<ClaimResponse> {

  public ClaimResponseFactory() {
    super(ClaimResponse.class, "test-data/");
  }

  public ClaimResponse build(ClaimExample example) {
    return super.load("ClaimResponse_" + example.name() + ".json");
  }

  /** Builds a Claim using ClaimExample.CONTRACT_ID */
  public ClaimResponse build() {
    return this.build(ClaimExample.CONTRACT_ID);
  }
}
