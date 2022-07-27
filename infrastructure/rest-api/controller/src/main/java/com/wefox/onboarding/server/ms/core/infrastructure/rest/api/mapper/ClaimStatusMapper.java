package com.wefox.onboarding.server.ms.core.infrastructure.rest.api.mapper;

import com.wefox.onboarding.server.ms.core.domain.enums.ClaimStatus;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.common.ClaimStatusDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

@Mapper(componentModel = "spring")
public interface ClaimStatusMapper {

  @ValueMappings({@ValueMapping(source = "ON_ERROR", target = MappingConstants.NULL)})
  ClaimStatusDTO toDto(ClaimStatus claimStatus);
}
