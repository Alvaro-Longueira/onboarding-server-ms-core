package com.wefox.onboarding.server.ms.core.application.port.input;

import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.server.lib.common.core.application.UseCase;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface CreateClaimUseCase extends UseCase<CreateClaimUseCase.InputValues, Claim> {

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  class InputValues {

    private String claimId;
    private String accountId;
    private OffsetDateTime notificationDate;
    private String type;
    private boolean withoutCoverage;
    @Builder.Default private Optional<String> description = Optional.empty();
    @Builder.Default private Optional<OffsetDateTime> eventDate = Optional.empty();
    @Builder.Default private Optional<String> placeOfEvent = Optional.empty();
    @Builder.Default private Optional<String> contractId = Optional.empty();
    @Builder.Default private Optional<String> offerId = Optional.empty();
    @Builder.Default private Optional<String> productId = Optional.empty();
    @Builder.Default private Optional<String> symassId = Optional.empty();
  }
}
