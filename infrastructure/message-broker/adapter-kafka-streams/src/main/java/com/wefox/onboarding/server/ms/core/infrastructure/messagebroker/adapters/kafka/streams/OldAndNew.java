package com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.streams;

import com.wefox.server.spec.avro.claims.entity.ClaimDTO;

public record OldAndNew(ClaimDTO current, ClaimDTO previous) {}
