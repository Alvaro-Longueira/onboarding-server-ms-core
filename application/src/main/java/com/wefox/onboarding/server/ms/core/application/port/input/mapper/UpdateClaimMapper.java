package com.wefox.onboarding.server.ms.core.application.port.input.mapper;

import com.wefox.onboarding.server.ms.core.application.port.input.UpdateClaimUseCase.InputValues;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UpdateClaimMapper {

  Claim merge(@MappingTarget Claim claim, InputValues inputValues);
}
