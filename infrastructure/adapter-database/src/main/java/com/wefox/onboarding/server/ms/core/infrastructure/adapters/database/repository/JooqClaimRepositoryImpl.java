package com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.repository;

import static org.jooq.impl.DSL.field;

import com.wefox.onboarding.server.ms.core.application.port.input.ClaimFilter;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.entity.ClaimDBEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@RequiredArgsConstructor
public class JooqClaimRepositoryImpl implements JooqClaimRepository {

  private final DSLContext dslContext;

  @Override
  public Page<ClaimDBEntity> findByFilter(ClaimFilter filter, PageRequest pageable) {
    final String table = "claims";
    List<Condition> whereClause = new ArrayList<>();
    if (filter.getAccountId() != null) {
      whereClause.add(field("account_id").equal(filter.getAccountId()));
    }
    if (filter.getContractId() != null) {
      whereClause.add(field("contract_id").equal(filter.getContractId()));
    }
    if (filter.getOfferId() != null) {
      whereClause.add(field("offer_id").equal(filter.getOfferId()));
    }
    int offset = pageable.getPageNumber() * pageable.getPageSize();
    int totalCount = dslContext.fetchCount(dslContext.select().from(table).where(whereClause));
    List<ClaimDBEntity> result =
        dslContext
            .select()
            .from(table)
            .where(whereClause)
            .offset(offset)
            .limit(pageable.getPageSize())
            .fetchInto(ClaimDBEntity.class);
    return new PageImpl<>(result, pageable, totalCount);
  }

  @Override
  public Optional<ClaimDBEntity> findByClaimId(String claimId) {
    final String table = "claims";

    return dslContext
        .select()
        .from(table)
        .where(field("claim_id").equal(claimId))
        .fetchOptionalInto(ClaimDBEntity.class);
  }
}
