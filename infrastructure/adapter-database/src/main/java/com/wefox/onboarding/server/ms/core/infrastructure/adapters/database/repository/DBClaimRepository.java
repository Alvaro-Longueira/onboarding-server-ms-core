package com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.repository;

import com.wefox.onboarding.server.ms.core.application.port.input.ClaimFilter;
import com.wefox.onboarding.server.ms.core.application.port.output.ClaimRepository;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.mapper.DBClaimMapper;
import com.wefox.server.lib.common.core.domain.pagination.Page;
import com.wefox.server.lib.common.core.domain.pagination.Pageable;
import com.wefox.server.lib.common.data.core.mapper.SpringPageMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class DBClaimRepository implements ClaimRepository {

  private final SpringDataClaimRepository repository;
  private final DBClaimMapper mapper;
  private final SpringPageMapper pageMapper;

  @Override
  public Optional<Claim> findByClaimId(String claimId) {
    var claim = repository.findByClaimId(claimId);
    return claim.map(mapper::toDomainEntity);
  }

  @Override
  public Claim save(Claim claim) {
    return mapper.toDomainEntity(repository.save(mapper.toDBEntity(claim)));
  }

  @Override
  public Page<Claim> findByFilter(ClaimFilter filter, Pageable pageable) {
    return pageMapper.toDomainModel(
        repository.findByFilter(
            filter, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())),
        mapper::toDomainEntity);
  }
}
