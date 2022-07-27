package com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.account.config;

import com.wefox.server.lib.common.web.client.WebClientBuilderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ComponentScan("com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.account")
@RequiredArgsConstructor
public class InfrastructureRestClientAccountAutoConfiguration {

  private final WebClientBuilderFactory webClientBuilderFactory;

  // @Primary
  @Bean("accountClient")
  public WebClient accountClient() {
    // configure in application.yml -> "common.web.client.configs.account-service:"
    return webClientBuilderFactory
        .getWebClientBuilder("account-service")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }
}
