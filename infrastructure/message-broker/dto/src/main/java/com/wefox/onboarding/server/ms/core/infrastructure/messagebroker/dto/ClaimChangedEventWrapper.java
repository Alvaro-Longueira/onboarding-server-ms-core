package com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wefox.server.lib.common.async.api.KeyedEventWrapper;
import com.wefox.server.spec.avro.claims.entity.ClaimDTO;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@SuperBuilder
@JsonNaming(SnakeCaseStrategy.class)
public class ClaimChangedEventWrapper extends KeyedEventWrapper<String, ClaimDTO> {}
