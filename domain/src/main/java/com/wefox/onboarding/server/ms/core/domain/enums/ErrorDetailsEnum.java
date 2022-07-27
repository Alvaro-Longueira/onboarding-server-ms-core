package com.wefox.onboarding.server.ms.core.domain.enums;

import com.wefox.server.lib.common.core.exception.ErrorDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorDetailsEnum implements ErrorDetails {
  COVERAGE_TYPE_NOT_COVERED(
      "CLAIM_DOM_001", "CoverageType not covered", "CoverageType not covered"),
  ACCOUNT_NOT_FOUND("CLAIM_DOM_002", "Account not found", "Account not found"),
  CLAIM_NOT_FOUND("CLAIM_DOM_003", "Claim not found", "Claim not found"),
  UNKNOWN_CLAIM_TYPE(
      "CLAIM_DOM_004", "Unknown claim type", "CoverageType not found for given ClaimType"),
  CREATE_CLAIM_VALIDATION(
      "CLAIM_DOM_005",
      "Create claim validation error",
      "One or more fields of the request contain invalid values."),
  UPDATE_STATUS_VALIDATION(
      "CLAIM_DOM_007", "Invalid claim status update", "Desired status transition is not allowed"),
  UPDATE_CLAIM_VALIDATION(
      "CLAIM_DOM_008",
      "Update claim validation error",
      "One or more fields from request contain invalid values"),
  COVERAGE_CONTRACT_NOT_FOUND("CLAIM_DOM_021", "Contract not found", "Contract not found"),
  NULL_CONTRACT_BODY("CLAIM_DOM_022", "Null contract body", "Contract body is null"),
  NULL_CONTRACT_ID("CLAIM_DOM_023", "Null contract id", "Contract id is null"),
  NULL_EVENT_DATE("CLAIM_DOM_024", "Null event date", "Event date is null");

  private final String code;
  private final String title;
  private final String detail;
}
