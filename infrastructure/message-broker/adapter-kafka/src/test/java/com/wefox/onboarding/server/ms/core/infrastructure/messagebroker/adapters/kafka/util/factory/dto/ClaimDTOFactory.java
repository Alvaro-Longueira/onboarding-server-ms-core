package com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.util.factory.dto;

import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.util.factory.example.ClaimExample;
import com.wefox.server.lib.common.test.factory.AbstractFactory;
import com.wefox.server.spec.avro.claims.entity.ClaimDTO;

public class ClaimDTOFactory extends AbstractFactory<ClaimDTO> {

  public ClaimDTOFactory() {
    super(ClaimDTO.class, "test-data/");
  }

  public ClaimDTO build(ClaimExample example) {
    return setUTCDates(super.load("Claim_" + example.name() + ".json"));
  }

  /** Builds a ClaimDTO using ClaimExample.CONTRACT_ID */
  public ClaimDTO build() {
    return this.build(ClaimExample.CONTRACT_ID);
  }

  private ClaimDTO setUTCDates(ClaimDTO dto) {
    dto.setEventDate(dto.getEventDate());
    dto.setEntryDate(dto.getEntryDate());
    dto.setNotificationDate(dto.getNotificationDate());
    return dto;
  }
}
