package com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.util.factory.event.ClaimChangedEventFactory;
import com.wefox.server.lib.common.async.api.KeyedEventWrapper;
import com.wefox.server.lib.common.stream.core.service.EventPublisherService;
import com.wefox.server.lib.common.stream.test.annotation.EventPublisherTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@EventPublisherTest
@ActiveProfiles("kafka")
public class EventPublisherServiceTests {

  private static final ClaimChangedEventFactory claimEventFactory = new ClaimChangedEventFactory();

  @Autowired private EventPublisherService eventPublisherService;
  @Autowired private OutputDestination outputDestination;

  @Test
  void send__givenClaimChangedEvent_whenSend_thenSendCorrectMessage() {
    var event = claimEventFactory.build();
    var binding = Bindings.CLAIM_CHANGED_EVENT;

    executeAndAssert(event, binding);
  }

  private void executeAndAssert(KeyedEventWrapper<?, ?> event, String binding) {
    var topic = "topic-" + binding;
    eventPublisherService.send(event, binding);

    var message = outputDestination.receive(1000, topic);
    var headers = message.getHeaders();
    assertTrue(headers.containsKey("key"));
    assertTrue(headers.containsKey("id"));
    assertTrue(headers.containsKey("timestamp"));
    var payload = (Object) message.getPayload();
    assertEquals(event.getValue(), payload);
  }
}
