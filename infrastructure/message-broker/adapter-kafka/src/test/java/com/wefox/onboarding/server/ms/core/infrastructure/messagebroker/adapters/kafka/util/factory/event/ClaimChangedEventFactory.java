package com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.util.factory.event;

import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.mapper.DTOMapper;
import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.mapper.DTOMapperImpl;
import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.util.factory.domain.ClaimFactory;
import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.util.factory.example.ClaimExample;
import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.dto.ClaimChangedEventWrapper;
import com.wefox.server.spec.avro.claims.entity.ClaimDTO;

public class ClaimChangedEventFactory {

  private final ClaimFactory entityFactory = new ClaimFactory();
  private final DTOMapper mapper = new DTOMapperImpl();

  public ClaimChangedEventWrapper build(ClaimExample example) {
    Claim entity = entityFactory.build(example);
    ClaimDTO dto = mapper.toDTO(entity);
    return ClaimChangedEventWrapper.builder().value(dto).key(dto.getId()).build();
  }

  /** Builds a Claim using ClaimExample.CONTRACT_ID */
  public ClaimChangedEventWrapper build() {
    return this.build(ClaimExample.CONTRACT_ID);
  }
}
