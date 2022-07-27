package com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.repository;

import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.entity.ClaimTypeSettingDBEntity;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataClaimTypeSettingRepository
    extends CrudRepository<ClaimTypeSettingDBEntity, Long> {

  @Query("SELECT * FROM claim_type_settings WHERE key_number_claim_type=:keyNumberClaimType")
  List<ClaimTypeSettingDBEntity> findByKeyNumberClaimType(
      @Param("keyNumberClaimType") String keyNumberClaimType);
}
