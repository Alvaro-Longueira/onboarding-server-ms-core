package com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka;

import com.wefox.onboarding.server.ms.core.application.port.output.ClaimChangedEventPublisher;
import com.wefox.onboarding.server.ms.core.application.util.ApplicationUtils;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.mapper.EventMapper;
import com.wefox.server.lib.common.stream.core.service.EventPublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClaimChangedEventPublisherImpl implements ClaimChangedEventPublisher {

  private final EventMapper eventMapper;
  private final EventPublisherService eventPublisherService;
  private final ApplicationUtils utils;

  @Override
  public void execute(ClaimChangedEventPublisher.Arguments<String, Claim> arguments) {
    if (utils.noSendEvents()) {
      return;
    }
    var event = eventMapper.toClaimChangedEventWrapper(arguments);
    eventPublisherService.send(event, Bindings.CLAIM_CHANGED_EVENT);
  }
}
