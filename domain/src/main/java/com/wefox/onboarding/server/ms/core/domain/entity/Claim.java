package com.wefox.onboarding.server.ms.core.domain.entity;

import com.wefox.onboarding.server.ms.core.domain.enums.ClaimStatus;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Claim {

  private String id;
  private Long internalId;
  private OffsetDateTime entryDate;
  private OffsetDateTime notificationDate;
  private ClaimStatus status = ClaimStatus.CREATED;
  private String accountId;
  private String type;
  private Boolean withoutCoverage;
  @Builder.Default private Optional<String> description = Optional.empty();
  @Builder.Default private Optional<OffsetDateTime> eventDate = Optional.empty();
  @Builder.Default private Optional<String> placeOfEvent = Optional.empty();
  @Builder.Default private Optional<String> contractId = Optional.empty();
  @Builder.Default private Optional<String> offerId = Optional.empty();
  @Builder.Default private Optional<String> coverageId = Optional.empty();
  @Builder.Default private Optional<String> insuranceId = Optional.empty();
  @Builder.Default private Optional<String> productId = Optional.empty();
  @Builder.Default private Optional<String> productDescription = Optional.empty();
  @Builder.Default private Optional<String> symassId = Optional.empty();
  private Long version;
  private OffsetDateTime createdAt;
  private String createdBy;
  private OffsetDateTime updatedAt;
  private String updatedBy;
}
