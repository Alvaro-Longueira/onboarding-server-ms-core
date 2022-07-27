package com.wefox.onboarding.server.ms.core.application.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "wefox.application")
public class ApplicationConfigProperties {

  /** Id of the market. */
  private String marketId = "de";

  /** Comma-separated list of markets to skip Symass validation. */
  private String symassSkip;

  /** Comma-separated list of markets to no send events to another systems */
  private String noSendEvents;
}
