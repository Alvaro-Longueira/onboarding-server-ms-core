package com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.repository;

import com.wefox.onboarding.server.ms.core.application.port.output.ClaimTypeSettingsRepository;
import com.wefox.onboarding.server.ms.core.domain.entity.ClaimTypeSetting;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.mapper.DBClaimTypeSettingMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class DBClaimTypeSettingRepository implements ClaimTypeSettingsRepository {

  private final SpringDataClaimTypeSettingRepository repository;
  private final DBClaimTypeSettingMapper mapper;

  @Override
  public Optional<ClaimTypeSetting> findCoverageType(String claimType) {
    return repository.findByKeyNumberClaimType(claimType).stream()
        .findFirst()
        .map(mapper::toDomainEntity);
  }
}
