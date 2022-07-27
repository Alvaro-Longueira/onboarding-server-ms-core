package com.wefox.onboarding.server.ms.core.application.port.input.interactor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wefox.onboarding.server.ms.core.application.port.input.FindAllClaimsUseCase.InputValues;
import com.wefox.onboarding.server.ms.core.application.port.output.ClaimRepository;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.server.lib.common.core.domain.pagination.Page;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindAllClaimsImplTest {

  @Mock private ClaimRepository claimRepository;

  @InjectMocks private FindAllClaimsImpl testUnit;

  private void givenValidFilterAndPagination() {
    when(claimRepository.findByFilter(any(), any())).thenReturn(new Page<>());
  }

  private void assertClaimRepositoryIsCalled() {
    verify(claimRepository, times(1)).findByFilter(any(), any());
  }

  @Test
  void successfulGetClaims() {
    givenValidFilterAndPagination();

    var testInput = defaultInputValues().build();

    Page<Claim> result = testUnit.execute(testInput);

    assertClaimRepositoryIsCalled();
  }

  private InputValues.InputValuesBuilder defaultInputValues() {
    return InputValues.builder();
  }
}
