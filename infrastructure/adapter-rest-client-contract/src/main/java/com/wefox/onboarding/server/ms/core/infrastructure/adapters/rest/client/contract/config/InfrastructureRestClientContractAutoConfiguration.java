package com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.config;

import com.wefox.server.lib.common.web.client.WebClientBuilderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ComponentScan("com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract")
@RequiredArgsConstructor
public class InfrastructureRestClientContractAutoConfiguration {

  private final WebClientBuilderFactory webClientBuilderFactory;

  @Bean("contractClient")
  public WebClient contractClient() {
    // configure in application.yml -> "common.web.client.configs.contract-service:"
    return webClientBuilderFactory
        .getWebClientBuilder("contract-service")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }
}
