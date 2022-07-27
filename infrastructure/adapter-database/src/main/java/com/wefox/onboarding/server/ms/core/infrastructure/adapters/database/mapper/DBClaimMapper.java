package com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.mapper;

import com.wefox.onboarding.server.ms.core.application.port.input.mapper.OptionalMapper;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.entity.ClaimDBEntity;
import com.wefox.server.lib.common.core.mapper.DateMapper;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
    uses = {DateMapper.class, OptionalMapper.class})
public interface DBClaimMapper {

  @Mapping(target = "id", source = "claimId")
  @Mapping(target = "internalId", source = "id")
  @Mapping(target = "symassId", source = "externalId")
  Claim toDomainEntity(ClaimDBEntity entity);

  @InheritInverseConfiguration
  ClaimDBEntity toDBEntity(Claim domain);
}
