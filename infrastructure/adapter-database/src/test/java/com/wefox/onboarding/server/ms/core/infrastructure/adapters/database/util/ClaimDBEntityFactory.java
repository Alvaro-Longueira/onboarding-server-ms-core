package com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.util;

import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.entity.ClaimDBEntity;
import com.wefox.server.lib.common.test.factory.AbstractFactory;

public class ClaimDBEntityFactory extends AbstractFactory<ClaimDBEntity> {

  public ClaimDBEntityFactory() {
    super(ClaimDBEntity.class, "test-data/");
  }

  public ClaimDBEntity build(ClaimExample example) {
    return super.load("ClaimDBEntity_" + example.name() + ".json");
  }

  /** Builds a ClaimDBEntity using ClaimExample.CONTRACT_ID */
  public ClaimDBEntity build() {
    return this.build(ClaimExample.CONTRACT_ID);
  }
}
