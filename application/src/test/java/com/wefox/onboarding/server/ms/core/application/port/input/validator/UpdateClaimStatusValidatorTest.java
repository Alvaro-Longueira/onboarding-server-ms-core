package com.wefox.onboarding.server.ms.core.application.port.input.validator;

import static com.wefox.onboarding.server.ms.core.application.port.input.validator.UpdateClaimStatusValidator.validate;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.wefox.onboarding.server.ms.core.domain.enums.ClaimStatus;
import com.wefox.server.lib.common.core.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class UpdateClaimStatusValidatorTest {

  @Test
  void givenValidStatusTransition_whenValidate_thenDoNothing() {
    assertDoesNotThrow(() -> validate(ClaimStatus.CREATED, ClaimStatus.CREATED));
    assertDoesNotThrow(() -> validate(ClaimStatus.CREATED, ClaimStatus.UNDER_REVIEW));
    assertDoesNotThrow(() -> validate(ClaimStatus.CREATED, ClaimStatus.PAID_OUT));
    assertDoesNotThrow(() -> validate(ClaimStatus.CREATED, ClaimStatus.REJECTED));
    assertDoesNotThrow(() -> validate(ClaimStatus.CREATED, ClaimStatus.WITHDRAWN));
    assertDoesNotThrow(() -> validate(ClaimStatus.UNDER_REVIEW, ClaimStatus.UNDER_REVIEW));
    assertDoesNotThrow(() -> validate(ClaimStatus.UNDER_REVIEW, ClaimStatus.PAID_OUT));
    assertDoesNotThrow(() -> validate(ClaimStatus.UNDER_REVIEW, ClaimStatus.REJECTED));
    assertDoesNotThrow(() -> validate(ClaimStatus.UNDER_REVIEW, ClaimStatus.WITHDRAWN));
    assertDoesNotThrow(() -> validate(ClaimStatus.PAID_OUT, ClaimStatus.PAID_OUT));
    assertDoesNotThrow(() -> validate(ClaimStatus.PAID_OUT, ClaimStatus.UNDER_REVIEW));
    assertDoesNotThrow(() -> validate(ClaimStatus.REJECTED, ClaimStatus.REJECTED));
    assertDoesNotThrow(() -> validate(ClaimStatus.REJECTED, ClaimStatus.UNDER_REVIEW));
    assertDoesNotThrow(() -> validate(ClaimStatus.WITHDRAWN, ClaimStatus.WITHDRAWN));
    assertDoesNotThrow(() -> validate(ClaimStatus.WITHDRAWN, ClaimStatus.UNDER_REVIEW));
  }

  @Test
  void givenInvalidStatusTransition_whenValidate_thenThrowsBusinessException() {
    Executable executable = () -> validate(ClaimStatus.PAID_OUT, ClaimStatus.CREATED);
    var exception = assertThrows(BusinessException.class, executable);
    var expectedCode = "CLAIM_DOM_007";
    assertEquals(expectedCode, exception.getCode());
  }
}
