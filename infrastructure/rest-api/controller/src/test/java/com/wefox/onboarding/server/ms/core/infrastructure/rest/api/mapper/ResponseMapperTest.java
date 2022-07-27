package com.wefox.onboarding.server.ms.core.infrastructure.rest.api.mapper;

import static com.wefox.server.lib.common.test.Assertions.assertSamePropertyValues;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.response.ClaimResponse;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.util.ClaimFactory;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.util.ClaimResponseFactory;
import com.wefox.server.lib.common.core.mapper.UTCDateMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResponseMapperTest {

  private final ClaimFactory claimFactory = new ClaimFactory();
  private final ClaimResponseFactory claimResponseFactory = new ClaimResponseFactory();
  private final EasyRandom generator = new EasyRandom();
  @Spy private final ClaimStatusMapper statusMapper = new ClaimStatusMapperImpl();
  @InjectMocks private final ResponseMapper mapper = new ResponseMapperImpl();

  @Test
  void Given_a_claim_When_mapped_claimToClaimResponse_Then_expected_equals_mapped() {
    // Given
    Claim original = claimFactory.build();
    ClaimResponse expected = claimResponseFactory.build();
    // additional transformations done in the mapper
    expected.setEntryDate(UTCDateMapper.map(expected.getEntryDate()));
    expected.setEventDate(UTCDateMapper.map(expected.getEventDate()));
    expected.setNotificationDate(UTCDateMapper.map(expected.getNotificationDate()));

    // When
    ClaimResponse mapped = mapper.claimToClaimResponse(original);

    // Then
    // check equals method
    assertEquals(expected, mapped, "equals");
    // check hashCode method
    assertEquals(expected.hashCode(), mapped.hashCode(), "hashCode");
    // check same property values
    assertSamePropertyValues(ClaimResponse.class, expected, mapped);
  }
}
