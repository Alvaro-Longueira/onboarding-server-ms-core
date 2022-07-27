package com.wefox.onboarding.server.ms.core.application.port.input.validator;

import static com.wefox.onboarding.server.ms.core.domain.enums.ErrorDetailsEnum.CREATE_CLAIM_VALIDATION;
import static com.wefox.onboarding.server.ms.core.domain.enums.ErrorDetailsEnum.UPDATE_CLAIM_VALIDATION;

import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.domain.enums.ErrorDetailsEnum;
import com.wefox.server.lib.common.core.exception.BusinessException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClaimValidator {

  private static final String CONTRACT_ID = "contract_id";
  private static final String OFFER_ID = "offer_id";
  private static final String OFFER_AND_CONTRACT_REASON =
      String.format("Request should not contain both %s and %s.", CONTRACT_ID, OFFER_ID);

  public static void validateCreation(Claim claim) {
    validate(claim, CREATE_CLAIM_VALIDATION, true);
  }

  public static void validateUpdate(Claim claim) {
    validate(claim, UPDATE_CLAIM_VALIDATION, false);
  }

  private static void validate(Claim claim, ErrorDetailsEnum errorDetail, boolean isCreate) {
    Map<String, String> invalidParams = new HashMap<>();
    validateDates(claim, invalidParams);
    if (isCreate) {
      validateWithoutCoverage(claim, invalidParams);
    }
    validateClaimType(claim, invalidParams);
    if (!invalidParams.isEmpty()) {
      throw new BusinessException(errorDetail, invalidParams);
    }
  }

  private static void validateDates(Claim claim, Map<String, String> invalidParams) {
    OffsetDateTime now = OffsetDateTime.now();
    if (claim.getEventDate().isPresent() && now.isBefore(claim.getEventDate().get())) {
      invalidParams.put("event_date", "Future dates not allowed.");
    }
    if (now.isBefore(claim.getNotificationDate())) {
      invalidParams.put("notification_date", "Future dates not allowed.");
    }
    if (claim.getEventDate().isPresent()
        && claim.getNotificationDate().isBefore(claim.getEventDate().get())) {
      invalidParams.put("notification_date", "Should be after event_date.");
    }
  }

  private static void validateWithoutCoverage(Claim claim, Map<String, String> invalidParams) {
    if (claim.getWithoutCoverage()) {
      validateWithoutCoverageTrue(claim, invalidParams);
    } else {
      if (claim.getContractId().isEmpty()) {
        invalidParams.put(
            CONTRACT_ID,
            String.format("%s should be provided, if without_coverage=false.", CONTRACT_ID));
      } else if (claim.getOfferId().isPresent()) {
        invalidParams.put(OFFER_ID, OFFER_AND_CONTRACT_REASON);
      }
    }
  }

  private static void validateWithoutCoverageTrue(Claim claim, Map<String, String> invalidParams) {
    boolean isOnlyOnePresent = claim.getOfferId().isPresent() ^ claim.getContractId().isPresent();
    if (!isOnlyOnePresent) {
      String withoutCoverageReasonTemplate =
          "%s or %s should be provided (just one of them), if without_coverage=true.";
      invalidParams.put(
          CONTRACT_ID, String.format(withoutCoverageReasonTemplate, CONTRACT_ID, OFFER_ID));
      invalidParams.put(
          OFFER_ID, String.format(withoutCoverageReasonTemplate, CONTRACT_ID, OFFER_ID));
    }
    if (StringUtils.isEmpty(claim.getProductId().orElse(""))) {
      invalidParams.put(
          "productId", "The product_id is required if without_coverage field is equal to true.");
    }
  }

  private static void validateClaimType(Claim claim, Map<String, String> invalidParams) {
    if (claim.getType().length() == 6) {
      validateClaimTypeValue(claim.getType(), invalidParams);
      return;
    }
    if (claim.getType().length() == 4) {
      validateClaimTypeValue(claim.getType(), invalidParams);
      claim.setType(claim.getType() + "00");
    } else {
      invalidParams.put(
          "type", String.format("Claim type: %s has invalid length.", claim.getType()));
    }
  }

  private static void validateClaimTypeValue(String type, Map<String, String> invalidParams) {
    for (int i = 0; i < type.length(); i++) {
      // check if any char in the string is not a number
      if (!((int) type.charAt(i) >= 48 && (int) type.charAt(i) <= 57)) {
        invalidParams.put("type", String.format("Claim type: %s must contain numbers only.", type));
        break;
      }
    }
  }
}
