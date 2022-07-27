package com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.mapper;

import com.wefox.onboarding.server.ms.core.domain.entity.ClaimTypeSetting;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.entity.ClaimTypeSettingDBEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface DBClaimTypeSettingMapper {

  ClaimTypeSetting toDomainEntity(ClaimTypeSettingDBEntity entity);
}
