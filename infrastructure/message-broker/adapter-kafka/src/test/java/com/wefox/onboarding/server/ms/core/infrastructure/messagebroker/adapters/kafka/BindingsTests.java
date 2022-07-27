package com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class BindingsTests {

  @Test
  void test() {
    var topic = "test";
    var expected = "topic-" + topic;
    var result = Bindings.topic(topic);
    assertEquals(expected, result);
  }
}
