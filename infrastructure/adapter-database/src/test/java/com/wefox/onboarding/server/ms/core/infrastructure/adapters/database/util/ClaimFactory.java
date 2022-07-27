package com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.util;

import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.server.lib.common.test.factory.AbstractFactory;

public class ClaimFactory extends AbstractFactory<Claim> {

  public ClaimFactory() {
    super(Claim.class, "test-data/");
  }

  public Claim build(ClaimExample example) {
    return super.load("Claim_" + example.name() + ".json");
  }

  /** Builds a Claim using ClaimExample.CONTRACT_ID */
  public Claim build() {
    return this.build(ClaimExample.CONTRACT_ID);
  }
}
