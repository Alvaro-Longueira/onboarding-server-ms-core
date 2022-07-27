package com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.mapper;

import com.wefox.onboarding.server.ms.core.domain.entity.Insurance;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.dto.contracts.InsuranceDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InsuranceMapper {
  Insurance toDomainEntity(InsuranceDto dto);
}
