package com.wefox.onboarding.server.ms.core.application.port.input.interactor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wefox.onboarding.server.ms.core.application.port.input.GetClaimByIdUseCase.InputValues;
import com.wefox.onboarding.server.ms.core.application.service.ClaimService;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetClaimByIdImplTest {

  @Mock private ClaimService claimService;

  @InjectMocks private GetClaimByIdImpl testUnit;

  private Claim givenValidClaimId() {
    var claim = new Claim();
    when(claimService.fetchClaim(any())).thenReturn(claim);
    return claim;
  }

  private void assertClaimServiceIsCalled(String claimId) {
    verify(claimService, times(1)).fetchClaim(eq(claimId));
  }

  @Test
  void successfulGetClaimById() {
    var claim = givenValidClaimId();
    var testInput = defaultInputValues().build();

    var result = testUnit.execute(testInput);
    assertClaimServiceIsCalled(testInput.getClaimId());
    assertEquals(claim, result);
  }

  private InputValues.InputValuesBuilder defaultInputValues() {
    return InputValues.builder().claimId("claimId");
  }
}
