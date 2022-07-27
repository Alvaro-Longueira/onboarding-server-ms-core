package com.wefox.onboarding.server.ms.core.application.port.output;

import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.domain.entity.ClaimTypeSetting;
import com.wefox.onboarding.server.ms.core.domain.entity.Insurance;
import java.util.Optional;

public interface ContractService {

  Optional<Insurance> checkCoverage(Claim claim, ClaimTypeSetting claimTypeSetting);
}
