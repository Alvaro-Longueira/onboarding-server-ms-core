package com.wefox.onboarding.server.ms.core.application.port.output;

import com.wefox.onboarding.server.ms.core.application.port.input.ClaimFilter;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.server.lib.common.core.domain.pagination.Page;
import com.wefox.server.lib.common.core.domain.pagination.Pageable;
import java.util.Optional;

public interface ClaimRepository {

  /**
   * When {@link Claim#getId()} is not null, an update will be attempted.
   *
   * @param claim the claim to store
   * @return the stored claim
   */
  Claim save(Claim claim);

  Optional<Claim> findByClaimId(String claimId);

  Page<Claim> findByFilter(ClaimFilter filter, Pageable pageable);
}
