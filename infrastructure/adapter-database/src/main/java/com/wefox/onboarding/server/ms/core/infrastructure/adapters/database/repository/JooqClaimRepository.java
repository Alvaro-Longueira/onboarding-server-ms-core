package com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.repository;

import com.wefox.onboarding.server.ms.core.application.port.input.ClaimFilter;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.entity.ClaimDBEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface JooqClaimRepository {

  Page<ClaimDBEntity> findByFilter(ClaimFilter filter, PageRequest pageable);

  Optional<ClaimDBEntity> findByClaimId(String claimId);
}
