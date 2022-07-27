package com.wefox.onboarding.server.ms.core.application.port.input.validator;

import static com.wefox.onboarding.server.ms.core.application.port.input.validator.ClaimValidator.validateCreation;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim.ClaimBuilder;
import com.wefox.server.lib.common.core.exception.BusinessException;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ClaimValidatorTest {

  @Test
  void givenEventDateInFuture_whenValidate_thenThrowsBusinessException() {
    var claim = defaultClaim().eventDate(Optional.of(OffsetDateTime.now().plusDays(1))).build();

    Executable executable = () -> validateCreation(claim);
    var exception = assertThrows(BusinessException.class, executable);
    var expectedCode = "CLAIM_DOM_005";
    assertEquals(expectedCode, exception.getCode());
  }

  @Test
  void given4DigitClaimType_whenValidate_thenTransformTo6Digit() {
    var claim = defaultClaim().type("0071").build();

    validateCreation(claim);
    var expectedType = "007100";
    assertEquals(expectedType, claim.getType());
    assertDoesNotThrow(() -> BusinessException.class);
  }

  @Test
  void givenInvalidDigitLengthClaimType_whenValidate_thenThrowsBusinessException() {
    var claim = defaultClaim().type("007").build();

    Executable executable = () -> validateCreation(claim);
    var exception = assertThrows(BusinessException.class, executable);
    var expectedCode = "CLAIM_DOM_005";
    var expectedInvalidParam = "Claim type: 007 has invalid length.";

    assertEquals(expectedCode, exception.getCode());
    assertEquals(expectedInvalidParam, exception.getInvalidParams().get("type"));
  }

  @Test
  void givenInvalidClaimTypeValue_whenValidate_thenThrowsBusinessException() {
    var claim = defaultClaim().type("abcd").build();

    Executable executable = () -> validateCreation(claim);
    var exception = assertThrows(BusinessException.class, executable);
    var expectedCode = "CLAIM_DOM_005";
    var expectedInvalidParam = "Claim type: abcd must contain numbers only.";

    assertEquals(expectedCode, exception.getCode());
    assertEquals(expectedInvalidParam, exception.getInvalidParams().get("type"));
  }

  @Test
  void givenNotificationDateInFuture_whenValidate_thenThrowsBusinessException() {
    var claim = defaultClaim().notificationDate(OffsetDateTime.now().plusDays(1)).build();

    Executable executable = () -> validateCreation(claim);
    var exception = assertThrows(BusinessException.class, executable);
    var expectedCode = "CLAIM_DOM_005";
    assertEquals(expectedCode, exception.getCode());
  }

  @Test
  void givenNotificationDateBeforeEventDate_whenValidate_thenThrowsBusinessException() {
    var claim =
        defaultClaim()
            .eventDate(Optional.of(OffsetDateTime.parse("2021-01-22T15:45:22+01:00")))
            .notificationDate(OffsetDateTime.parse("2020-12-12T10:10:10+01:00"))
            .build();

    Executable executable = () -> validateCreation(claim);
    var exception = assertThrows(BusinessException.class, executable);
    var expectedCode = "CLAIM_DOM_005";
    assertEquals(expectedCode, exception.getCode());
  }

  @Test
  void givenNoEventDate_whenValidate_thenSuccess() {
    var claim = defaultClaim().eventDate(Optional.empty()).build();
    validateCreation(claim);
  }

  @Test
  void givenContractAndNoOfferAndWithoutCoverageIsTrue_whenValidate_thenSuccess() {
    var claim = defaultClaim().withoutCoverage(true).build();

    Executable executable = () -> validateCreation(claim);
    assertDoesNotThrow(executable);
  }

  @Test
  void givenOfferAndNoContractAndWithoutCoverageIsTrue_whenValidate_thenSuccess() {
    var claim =
        defaultClaim()
            .offerId(getOfferId())
            .contractId(Optional.empty())
            .withoutCoverage(true)
            .build();

    Executable executable = () -> validateCreation(claim);
    assertDoesNotThrow(executable);
  }

  @Test
  void givenNoOfferOrContractAndWithoutCoverageIsTrue_whenValidate_thenThrowsBusinessException() {
    var claim = defaultClaim().contractId(Optional.empty()).withoutCoverage(true).build();

    Executable executable = () -> validateCreation(claim);
    var exception = assertThrows(BusinessException.class, executable);
    var expectedCode = "CLAIM_DOM_005";
    assertEquals(expectedCode, exception.getCode());
  }

  @Test
  void givenContractIdAndWithoutCoverageIsTrue_whenValidate_thenThrowsBusinessException() {
    var claim = defaultClaim().withoutCoverage(true).offerId(getOfferId()).build();

    Executable executable = () -> validateCreation(claim);
    var exception = assertThrows(BusinessException.class, executable);
    var expectedCode = "CLAIM_DOM_005";
    assertEquals(expectedCode, exception.getCode());
  }

  @Test
  void givenOfferIdAndWithoutCoverageIsFalse_whenValidate_thenThrowsBusinessException() {
    var claim =
        defaultClaim()
            .contractId(Optional.empty())
            .offerId(getOfferId())
            .withoutCoverage(false)
            .build();

    Executable executable = () -> validateCreation(claim);
    var exception = assertThrows(BusinessException.class, executable);
    var expectedCode = "CLAIM_DOM_005";
    assertEquals(expectedCode, exception.getCode());
  }

  @Test
  void givenBothContractAndOfferData_whenValidate_thenThrowsBusinessException() {
    var claim = defaultClaim().offerId(getOfferId()).build();

    Executable executable = () -> validateCreation(claim);
    var exception = assertThrows(BusinessException.class, executable);
    var expectedCode = "CLAIM_DOM_005";
    assertEquals(expectedCode, exception.getCode());
  }

  @Test
  void givenWithoutCoverageTrueAndProductIdIsEmpty_whenValidate_thenThrowsBusinessException() {
    var claim = defaultClaim().withoutCoverage(true).productId(Optional.empty()).build();

    Executable executable = () -> validateCreation(claim);
    var exception = assertThrows(BusinessException.class, executable);
    var expectedCode = "CLAIM_DOM_005";
    assertEquals(expectedCode, exception.getCode());
  }

  private ClaimBuilder defaultClaim() {
    return Claim.builder()
        .id("claimId")
        .eventDate(Optional.of(OffsetDateTime.parse("2020-12-12T10:10:10+01:00")))
        .notificationDate(OffsetDateTime.parse("2021-01-22T15:45:22+01:00"))
        .contractId(Optional.of("contractId"))
        .accountId("accountId")
        .type("007100")
        .coverageId(Optional.of("coverageId"))
        .productId(Optional.of("productId"))
        .insuranceId(Optional.of("insuranceId"))
        .withoutCoverage(false);
  }

  private Optional<String> getOfferId() {
    return Optional.of("offerId");
  }
}
