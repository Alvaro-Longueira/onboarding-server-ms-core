package com.wefox.onboarding.server.ms.core.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.wefox.onboarding.server.ms.core.application.port.output.ClaimChangedEventPublisher;
import com.wefox.onboarding.server.ms.core.application.port.output.ClaimRepository;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.server.lib.common.core.exception.NotFoundException;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTests {

  private static final Claim TEST_CLAIM = new Claim();
  @Captor ArgumentCaptor<ClaimChangedEventPublisher.Arguments<String, Claim>> eventCaptor;
  @Mock private ClaimRepository claimRepository;
  @Mock private ClaimChangedEventPublisher eventPublisher;
  @InjectMocks private ClaimService claimService;

  @Test
  void fetchClaim__givenExistingClaim_whenFetch_thenReturnClaim() {
    givenExistingClaim();
    var claim = claimService.fetchClaim("claimId");
    assertEquals(TEST_CLAIM, claim);
  }

  @Test
  void fetchClaim__givenNonExistingClaim_whenFetch_thenThrowsBusinessException() {
    givenNonExistingClaim();

    Executable executable = () -> claimService.fetchClaim("claimId");
    var exception = assertThrows(NotFoundException.class, executable);
    var expectedMessage = "CLAIM_DOM_003";
    assertEquals(expectedMessage, exception.getCode());
  }

  @Test
  void createClaim() {
    when(claimRepository.save(any())).thenAnswer(c -> c.getArgument(0));
    var claim = new Claim();
    claim.setId(UUID.randomUUID().toString());
    claim.setEventDate(Optional.of(OffsetDateTime.now()));
    claimService.storeClaim(claim);
    verify(claimRepository, times(1)).save(any());
    verify(eventPublisher, times(1)).execute(eventCaptor.capture());
    assertEquals(claim.getId(), eventCaptor.getValue().getKey());
  }

  @Test
  void updateClaim() {
    when(claimRepository.save(any())).thenAnswer(c -> c.getArgument(0));
    var claim = new Claim();
    claim.setId(UUID.randomUUID().toString());
    claim.setEventDate(Optional.of(OffsetDateTime.now()));
    claimService.storeClaim(claim);
    verify(claimRepository, times(1)).save(any());
    verify(eventPublisher, times(1)).execute(eventCaptor.capture());
    assertEquals(claim.getId(), eventCaptor.getValue().getKey());
  }

  @Test
  void givenNoEventDate_whenStoreClaim_thenPublishIsSkipped() {
    var claim = new Claim();
    when(claimRepository.save(claim)).thenReturn(claim);
    claimService.storeClaim(claim);
    verify(claimRepository, times(1)).save(claim);
    verifyNoInteractions(eventPublisher);
  }

  private void givenExistingClaim() {
    when(claimRepository.findByClaimId(any())).thenReturn(Optional.of(TEST_CLAIM));
  }

  private void givenNonExistingClaim() {
    when(claimRepository.findByClaimId(any())).thenReturn(Optional.empty());
  }
}
