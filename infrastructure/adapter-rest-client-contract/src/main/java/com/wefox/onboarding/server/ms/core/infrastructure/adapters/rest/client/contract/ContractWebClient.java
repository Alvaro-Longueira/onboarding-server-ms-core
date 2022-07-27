package com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract;

import com.wefox.onboarding.server.ms.core.application.config.ApplicationConfigProperties;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.dto.contracts.ContractDto;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.dto.contracts.limits.ContractLimitsDto;
import java.time.OffsetDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractWebClient {

  private static final String CONTRACT_URI = "api/{marketId}/contract/v1/contracts/{contractId}";

  private final ApplicationConfigProperties properties;

  private final WebClient contractClient;

  public ResponseEntity<ContractDto> getContract(String contractId, OffsetDateTime validOnDate) {
    Mono<ResponseEntity<ContractDto>> result =
        contractClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path(CONTRACT_URI)
                        .queryParam("validOnDate", validOnDate.toInstant().toEpochMilli())
                        .build(properties.getMarketId(), contractId))
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
            .toEntity(ContractDto.class);

    ResponseEntity<ContractDto> response = Objects.requireNonNull(result.block());
    log.info("Response contracts status {} body {}", response.getStatusCode(), response.getBody());
    return response;
  }

  public ResponseEntity<ContractLimitsDto> getContractLimits(
      String contractId, OffsetDateTime validOnDate) {
    Mono<ResponseEntity<ContractLimitsDto>> result =
        contractClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path(CONTRACT_URI + "/limits")
                        .queryParam("validOnDate", validOnDate.toInstant().toEpochMilli())
                        .build(properties.getMarketId(), contractId))
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
            .toEntity(ContractLimitsDto.class);

    ResponseEntity<ContractLimitsDto> response = Objects.requireNonNull(result.block());
    log.info(
        "Response contractLimits status {} body {}", response.getStatusCode(), response.getBody());
    return response;
  }
}
