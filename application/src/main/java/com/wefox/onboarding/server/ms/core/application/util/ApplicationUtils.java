package com.wefox.onboarding.server.ms.core.application.util;

import com.wefox.onboarding.server.ms.core.application.config.ApplicationConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationUtils {

  private final ApplicationConfigProperties properties;

  public boolean skipSymass() {
    return properties.getSymassSkip().contains(properties.getMarketId());
  }

  public boolean noSendEvents() {
    return properties.getNoSendEvents().contains(properties.getMarketId());
  }
}
