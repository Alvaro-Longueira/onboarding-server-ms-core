package com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wefox.onboarding.server.ms.core.domain.enums.ClaimStatus;
import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.util.factory.domain.ClaimFactory;
import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.util.factory.dto.ClaimDTOFactory;
import org.junit.jupiter.api.Test;

class DTOMapperTest {

  private static final DTOMapper mapper = new DTOMapperImpl();

  @Test
  void toDTO__givenValidClaim_whenMap_thenReturnExpectedDTO() {
    var input = new ClaimFactory().build();
    var result = mapper.toDTO(input);
    var expected = new ClaimDTOFactory().build();
    assertEquals(expected, result);
  }

  @Test
  void toDTO__givenValidClaimStatus_whenMap_thenReturnExpectedDTO() {
    var input = ClaimStatus.CREATED;
    var result = mapper.toDTO(input);
    var expected = com.wefox.server.spec.avro.claims.enums.ClaimStatus.CREATED;
    assertEquals(expected, result);
  }
}
