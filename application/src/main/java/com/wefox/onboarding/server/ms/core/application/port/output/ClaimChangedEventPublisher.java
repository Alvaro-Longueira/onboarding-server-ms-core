package com.wefox.onboarding.server.ms.core.application.port.output;

import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.server.lib.common.core.application.AbstractKeyedEventPublisher;

public interface ClaimChangedEventPublisher extends AbstractKeyedEventPublisher<String, Claim> {}
