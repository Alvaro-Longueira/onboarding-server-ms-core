package com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.dto.contracts.limits;

import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming
public record ContractLimitDto(
    String contractLimitType, Long insuranceId, String insuredEventClass) {}
