package com.wefox.onboarding.server.ms.core.application.port.input.interactor;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wefox.onboarding.server.ms.core.application.port.input.UpdateClaimStatusUseCase.InputValues;
import com.wefox.onboarding.server.ms.core.application.service.ClaimService;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.domain.enums.ClaimStatus;
import com.wefox.server.lib.common.core.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateClaimStatusImplTest {

  @Mock private ClaimService claimService;

  @InjectMocks private UpdateClaimStatusImpl testUnit;

  @Test
  void successfulStatusUpdate() {
    givenClaimStatusCreated();

    var testInput = defaultInputValues().build();

    testUnit.execute(testInput);
    assertClaimIsSaved();
  }

  @Test
  void unsuccessfulStatusUpdate_invalidStatusTransition() {
    givenClaimStatusRejected();

    var testInput = defaultInputValues().status(ClaimStatus.PAID_OUT).build();

    Executable executable = () -> testUnit.execute(testInput);
    BusinessException exception = assertThrows(BusinessException.class, executable);

    var expectedCode = "CLAIM_DOM_007";
    assertEquals(expectedCode, exception.getCode());
    assertClaimIsNotSaved();
  }

  private void givenClaimStatusCreated() {
    Claim claim = new Claim();
    claim.setStatus(ClaimStatus.CREATED);
    when(claimService.fetchClaim(any())).thenReturn(claim);
  }

  private void givenClaimStatusRejected() {
    Claim claim = new Claim();
    claim.setStatus(ClaimStatus.REJECTED);
    when(claimService.fetchClaim(any())).thenReturn(claim);
  }

  private void assertClaimIsSaved() {
    assertAll(() -> verify(claimService, times(1)).storeClaim(any()));
  }

  private void assertClaimIsNotSaved() {
    assertAll(() -> verify(claimService, times(0)).storeClaim(any()));
  }

  private static InputValues.InputValuesBuilder defaultInputValues() {
    return InputValues.builder().claimId("claimId").status(ClaimStatus.UNDER_REVIEW);
  }
}
