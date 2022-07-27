package com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.converter;

import com.wefox.onboarding.server.ms.core.domain.enums.ClaimStatus;
import org.jooq.impl.EnumConverter;
import org.springframework.stereotype.Component;

@Component
public class JooqStatusConverter extends EnumConverter<String, ClaimStatus> {

  public JooqStatusConverter() {
    super(String.class, ClaimStatus.class);
  }
}
