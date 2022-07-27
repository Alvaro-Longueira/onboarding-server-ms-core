package com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.entity;

import com.wefox.onboarding.server.ms.core.domain.enums.ClaimStatus;
import com.wefox.server.lib.common.data.core.audit.entity.AbstractAuditableEntity;
import java.time.OffsetDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Table("claims")
public class ClaimDBEntity extends AbstractAuditableEntity {

  @Id private Long id;

  @Column("claim_id")
  @Size(max = 255)
  @NotNull
  private String claimId;

  @Column("event_date")
  @NotNull
  private OffsetDateTime eventDate;

  @Column("notification_date")
  @NotNull
  private OffsetDateTime notificationDate;

  @Column("entry_date")
  @NotNull
  private OffsetDateTime entryDate;

  @Column private String description;

  @Column("place_of_event")
  private String placeOfEvent;

  @Column("contract_id")
  @Size(max = 255)
  private String contractId;

  @Column("offer_id")
  @Size(max = 255)
  private String offerId;

  @Column("account_id")
  @Size(max = 255)
  @NotNull
  private String accountId;

  @Column("type")
  @NotNull
  private String type;

  @Column("coverage_id")
  @Size(max = 255)
  @NotNull
  private String coverageId;

  @Column("status")
  @NotNull
  private ClaimStatus status = ClaimStatus.CREATED;

  @Column("product_id")
  @Size(max = 255)
  private String productId;

  @Column("product_description")
  @Size(max = 255)
  private String productDescription;

  @Column("insurance_id")
  @Size(max = 255)
  private String insuranceId;

  @Column("without_coverage")
  @NotNull
  private Boolean withoutCoverage;

  @Column("external_id")
  @Size(max = 255)
  private String externalId;
}
