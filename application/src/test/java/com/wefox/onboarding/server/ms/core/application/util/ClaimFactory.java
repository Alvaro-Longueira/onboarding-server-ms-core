package com.wefox.onboarding.server.ms.core.application.util;

import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.server.lib.common.test.factory.AbstractFactory;
import java.util.function.Consumer;

public class ClaimFactory extends AbstractFactory<Claim> {

  public ClaimFactory() {
    super(Claim.class, "test-data/");
  }

  public Claim build(ClaimExample example, Consumer<Claim> c) {
    return super.load("Claim_" + example.name() + ".json", c);
  }

  /** Builds a Claim using ClaimExample.CONTRACT_ID */
  public Claim build(Consumer<Claim> c) {
    return this.build(ClaimExample.CONTRACT_ID, c);
  }

  public Claim build() {
    return this.build(ClaimExample.CONTRACT_ID, c -> {});
  }
}
