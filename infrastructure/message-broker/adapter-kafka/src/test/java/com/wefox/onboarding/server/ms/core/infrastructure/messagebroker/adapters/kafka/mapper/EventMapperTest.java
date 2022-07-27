package com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.wefox.onboarding.server.ms.core.application.config.ApplicationConfigProperties;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.config.InfrastructureKafkaAutoConfiguration;
import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.util.factory.domain.ClaimFactory;
import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.util.factory.event.ClaimChangedEventFactory;
import com.wefox.server.lib.common.core.application.AbstractKeyedEventPublisher;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = InfrastructureKafkaAutoConfiguration.class)
class EventMapperTest {

  private static final DTOMapper dtoMapper = new DTOMapperImpl();
  private static final EventMapper mapper = new EventMapperImpl(dtoMapper);
  private static final ApplicationConfigProperties configProperties =
      new ApplicationConfigProperties();

  static {
    mapper.setConfigProperties(configProperties);
  }

  @Test
  void toClaimEvent__givenValidClaimArgs_whenMap_thenReturnExpectedEvent() {
    var entity = new ClaimFactory().build();
    var input =
        AbstractKeyedEventPublisher.Arguments.<String, Claim>builder()
            .key(entity.getId())
            .value(entity)
            .build();
    var result = mapper.toClaimChangedEventWrapper(input);
    var expected = new ClaimChangedEventFactory().build();
    assertEquals(expected, result);
    assertNotNull(result.getId());
    assertNotNull(result.getTimestamp());
    assertEquals(ZoneOffset.UTC, result.getTimestamp().getOffset());
    assertNotNull(result.getHeaders());
    assertNotNull(result.getHeaders().get(EventMapper.MARKET_HEADER));
    assertEquals(
        result.getHeaders().get(EventMapper.MARKET_HEADER), configProperties.getMarketId());
  }
}
