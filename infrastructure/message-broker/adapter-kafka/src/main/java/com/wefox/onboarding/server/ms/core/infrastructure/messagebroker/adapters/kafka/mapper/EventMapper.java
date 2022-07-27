package com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.mapper;

import com.wefox.onboarding.server.ms.core.application.config.ApplicationConfigProperties;
import com.wefox.onboarding.server.ms.core.application.port.output.ClaimChangedEventPublisher;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.dto.ClaimChangedEventWrapper;
import com.wefox.server.lib.common.core.mapper.UTCDateMapper;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Setter;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    componentModel = "spring",
    imports = {UUID.class, OffsetDateTime.class, UTCDateMapper.class},
    uses = {UTCDateMapper.class, DTOMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@Setter
public abstract class EventMapper {

  static final String MARKET_HEADER = "x-market";

  @Autowired protected ApplicationConfigProperties configProperties;

  @EventMappings
  public abstract ClaimChangedEventWrapper toClaimChangedEventWrapper(
      ClaimChangedEventPublisher.Arguments<String, Claim> args);

  @Mapping(target = "id", expression = "java(UUID.randomUUID())")
  @Mapping(target = "timestamp", expression = "java(UTCDateMapper.map(OffsetDateTime.now()))")
  @Mapping(target = "headers", expression = "java(addMarketHeader())")
  @interface EventMappings {}

  protected Map<String, String> addMarketHeader() {
    var headers = new HashMap<String, String>();
    headers.put(MARKET_HEADER, configProperties.getMarketId());
    return headers;
  }
}
