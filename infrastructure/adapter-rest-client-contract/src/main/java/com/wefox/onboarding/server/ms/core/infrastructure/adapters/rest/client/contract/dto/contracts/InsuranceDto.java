package com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.dto.contracts;

import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming
public record InsuranceDto(
    Long id, String insuranceType, String productType, Long productId, String productDescription) {}
