package com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.mapper;

import com.wefox.onboarding.server.ms.core.application.port.input.mapper.OptionalMapper;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.domain.enums.ClaimStatus;
import com.wefox.server.lib.common.core.mapper.UTCDateMapper;
import com.wefox.server.spec.avro.claims.entity.ClaimDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

@Mapper(
    componentModel = "spring",
    uses = {UTCDateMapper.class, OptionalMapper.class})
public interface DTOMapper {

  ClaimDTO toDTO(Claim domain);

  @ValueMappings({@ValueMapping(source = "ON_ERROR", target = MappingConstants.NULL)})
  com.wefox.server.spec.avro.claims.enums.ClaimStatus toDTO(ClaimStatus domain);
}
