package com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Bindings {

  public static final String CLAIM_CHANGED_EVENT = "claim-changed-event";

  public static String topic(String binding) {
    return "topic-" + binding;
  }
}
