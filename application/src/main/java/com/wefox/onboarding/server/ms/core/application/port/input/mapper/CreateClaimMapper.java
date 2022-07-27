package com.wefox.onboarding.server.ms.core.application.port.input.mapper;

import com.wefox.onboarding.server.ms.core.application.port.input.CreateClaimUseCase;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {OptionalMapper.class})
public interface CreateClaimMapper {

  @Mapping(target = "id", source = "claimId")
  @Mapping(target = "status", constant = "CREATED")
  Claim toDomainEntity(CreateClaimUseCase.InputValues inputValues);
}
