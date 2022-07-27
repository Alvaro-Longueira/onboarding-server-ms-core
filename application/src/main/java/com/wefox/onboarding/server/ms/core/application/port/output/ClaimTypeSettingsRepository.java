package com.wefox.onboarding.server.ms.core.application.port.output;

import com.wefox.onboarding.server.ms.core.domain.entity.ClaimTypeSetting;
import java.util.Optional;

public interface ClaimTypeSettingsRepository {

  Optional<ClaimTypeSetting> findCoverageType(String claimType);
}
