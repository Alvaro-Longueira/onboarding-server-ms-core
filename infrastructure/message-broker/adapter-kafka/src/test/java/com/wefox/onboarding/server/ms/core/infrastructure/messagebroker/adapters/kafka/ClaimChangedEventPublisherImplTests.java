package com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.wefox.onboarding.server.ms.core.application.util.ApplicationUtils;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.mapper.EventMapper;
import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.util.factory.domain.ClaimFactory;
import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.dto.ClaimChangedEventWrapper;
import com.wefox.server.lib.common.core.application.AbstractKeyedEventPublisher;
import com.wefox.server.lib.common.stream.core.service.EventPublisherService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ClaimChangedEventPublisherImplTests {

  private ClaimFactory claimFactory = new ClaimFactory();
  @Mock private EventMapper eventMapper;
  @Mock private EventPublisherService eventPublisherService;
  @Mock private ApplicationUtils utils;
  @InjectMocks private ClaimChangedEventPublisherImpl testUnit;

  @Test
  void givenValidArguments_whenExecute_thenMapToEventAndPublish() {
    utilsConfigurationNoSendEvents(false);
    var arguments = defaultArguments();
    var event = givenSuccessfulEventMapping();
    testUnit.execute(arguments);
    assertEventMappingIsCalled(arguments);
    assertEventPublisherServiceIsCalled(event);
  }

  @Test
  void given_configNoSendEventsEnabled_whenExecute_thenThereIsNoInteractions() {
    utilsConfigurationNoSendEvents(true);
    var arguments = defaultArguments();
    testUnit.execute(arguments);
    verifyNoInteractions(eventMapper);
    verifyNoInteractions(eventPublisherService);
  }

  private ClaimChangedEventWrapper givenSuccessfulEventMapping() {
    var event = defaultEvent();
    when(eventMapper.toClaimChangedEventWrapper(any())).thenReturn(event);
    return event;
  }

  private void assertEventMappingIsCalled(AbstractKeyedEventPublisher.Arguments arguments) {
    verify(eventMapper, times(1)).toClaimChangedEventWrapper(eq(arguments));
  }

  private void assertEventPublisherServiceIsCalled(ClaimChangedEventWrapper event) {
    verify(eventPublisherService, times(1)).send(eq(event), eq(Bindings.CLAIM_CHANGED_EVENT));
  }

  private static AbstractKeyedEventPublisher.Arguments<String, Claim> defaultArguments() {
    return AbstractKeyedEventPublisher.Arguments.<String, Claim>builder()
        .value(new Claim())
        .key(UUID.randomUUID().toString())
        .build();
  }

  private static ClaimChangedEventWrapper defaultEvent() {
    return ClaimChangedEventWrapper.builder().build();
  }

  private void utilsConfigurationNoSendEvents(boolean enabled) {
    when(utils.noSendEvents()).thenReturn(enabled);
  }
}
