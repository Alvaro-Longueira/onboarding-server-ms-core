package com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.repository;

import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.entity.ClaimDBEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataClaimRepository
    extends PagingAndSortingRepository<ClaimDBEntity, Long>, JooqClaimRepository {}
