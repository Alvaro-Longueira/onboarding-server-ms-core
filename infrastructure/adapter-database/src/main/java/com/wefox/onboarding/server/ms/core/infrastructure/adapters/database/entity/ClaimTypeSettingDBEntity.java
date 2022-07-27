package com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.entity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@Builder
@Table("claim_type_settings")
public class ClaimTypeSettingDBEntity {

  @Id private Long id;

  @Column("key_number_claim_type")
  @Size(max = 255)
  @NotNull
  private String keyNumberClaimType;

  @Column("insured_event_class")
  @Size(max = 255)
  @NotNull
  private String insuredEventClass;

  @Column("claim_type")
  @Size(max = 255)
  @NotNull
  private String claimType;

  @Column("coverage_type")
  @Size(max = 255)
  private String coverageType;
}
