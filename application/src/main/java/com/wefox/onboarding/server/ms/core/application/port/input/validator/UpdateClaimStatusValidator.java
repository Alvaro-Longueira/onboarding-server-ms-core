package com.wefox.onboarding.server.ms.core.application.port.input.validator;

import static com.wefox.onboarding.server.ms.core.domain.enums.ClaimStatus.CREATED;
import static com.wefox.onboarding.server.ms.core.domain.enums.ClaimStatus.PAID_OUT;
import static com.wefox.onboarding.server.ms.core.domain.enums.ClaimStatus.REJECTED;
import static com.wefox.onboarding.server.ms.core.domain.enums.ClaimStatus.UNDER_REVIEW;
import static com.wefox.onboarding.server.ms.core.domain.enums.ClaimStatus.WITHDRAWN;

import com.wefox.onboarding.server.ms.core.domain.enums.ClaimStatus;
import com.wefox.onboarding.server.ms.core.domain.enums.ErrorDetailsEnum;
import com.wefox.server.lib.common.core.exception.BusinessException;
import java.util.Arrays;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateClaimStatusValidator {

  private static final MultiValueMap<ClaimStatus, ClaimStatus> validTransitions =
      new LinkedMultiValueMap<>();

  static {
    validTransitions.addAll(
        CREATED, Arrays.asList(CREATED, UNDER_REVIEW, REJECTED, PAID_OUT, WITHDRAWN));
    validTransitions.addAll(
        UNDER_REVIEW, Arrays.asList(UNDER_REVIEW, REJECTED, PAID_OUT, WITHDRAWN));
    validTransitions.addAll(REJECTED, Arrays.asList(REJECTED, UNDER_REVIEW));
    validTransitions.addAll(PAID_OUT, Arrays.asList(PAID_OUT, UNDER_REVIEW));
    validTransitions.addAll(WITHDRAWN, Arrays.asList(WITHDRAWN, UNDER_REVIEW));
  }

  public static void validate(ClaimStatus oldStatus, ClaimStatus newStatus) {
    var transition = validTransitions.get(oldStatus);
    if (Objects.isNull(transition) || !transition.contains(newStatus)) {
      throw new BusinessException(ErrorDetailsEnum.UPDATE_STATUS_VALIDATION);
    }
  }
}
