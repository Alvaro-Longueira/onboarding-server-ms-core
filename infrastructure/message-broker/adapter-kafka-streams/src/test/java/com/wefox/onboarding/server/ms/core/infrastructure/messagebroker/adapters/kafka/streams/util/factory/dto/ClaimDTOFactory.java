package com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.streams.util.factory.dto;

import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.streams.util.factory.example.ClaimExample;
import com.wefox.server.lib.common.test.factory.AbstractFactory;
import com.wefox.server.spec.avro.claims.entity.ClaimDTO;
import java.util.function.Consumer;

public class ClaimDTOFactory extends AbstractFactory<ClaimDTO> {

  // Do nothing
  private Consumer<ClaimDTO> utcDates =
      dto -> {
        dto.setEventDate(dto.getEventDate());
        dto.setEntryDate(dto.getEntryDate());
        dto.setNotificationDate(dto.getNotificationDate());
      };

  public ClaimDTOFactory() {
    super(ClaimDTO.class, "test-data/");
  }

  public ClaimDTO build(ClaimExample example) {
    return build(example, o -> {});
  }

  public ClaimDTO build(ClaimExample example, Consumer<ClaimDTO> consumer) {
    return super.load("Claim_" + example.name() + ".json", utcDates.andThen(consumer));
  }

  /** Builds a ClaimDTO using ClaimExample.CONTRACT_ID */
  public ClaimDTO build() {
    return this.build(ClaimExample.CONTRACT_ID);
  }

  /** Builds a ClaimDTO using ClaimExample.CONTRACT_ID */
  public ClaimDTO build(Consumer<ClaimDTO> consumer) {
    return this.build(ClaimExample.CONTRACT_ID, consumer);
  }
}
