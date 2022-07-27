package com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.account;

import com.wefox.onboarding.server.ms.core.application.config.ApplicationConfigProperties;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AccountWebClient {

  private final ApplicationConfigProperties properties;

  @Qualifier("accountClient")
  private final WebClient accountClient;

  public HttpStatus getAccount(String accountId) {
    Mono<ResponseEntity<String>> result =
        accountClient
            .get()
            .uri("api/" + properties.getMarketId() + "/partner-core/v1/partners/" + accountId)
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
            .toEntity(String.class);
    return Objects.requireNonNull(result.block()).getStatusCode();
  }
}
