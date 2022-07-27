package com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.dto.contracts;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming
public record ContractDto(String contractNumber, List<InsuranceDto> insurances) {}
