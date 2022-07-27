package com.wefox.onboarding.server.ms.core.infrastructure.rest.api.mapper;

import com.wefox.onboarding.server.ms.core.application.port.input.mapper.OptionalMapper;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.response.ClaimResponse;
import com.wefox.server.lib.common.core.mapper.UTCDateMapper;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
    componentModel = "spring",
    uses = {UTCDateMapper.class, ClaimStatusMapper.class, OptionalMapper.class})
public abstract class ResponseMapper {

  @Mapping(target = "claimId", source = "id")
  @Mapping(target = "eventDate", source = "eventDate", qualifiedByName = "eventDateWithOptional")
  public abstract ClaimResponse claimToClaimResponse(Claim claim);

  @Named("eventDateWithOptional")
  public static OffsetDateTime getEventDateWithOptional(Optional<OffsetDateTime> eventDate) {
    return UTCDateMapper.map(eventDate.orElse(null));
  }
}
