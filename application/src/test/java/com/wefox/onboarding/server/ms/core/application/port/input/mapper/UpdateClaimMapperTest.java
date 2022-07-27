package com.wefox.onboarding.server.ms.core.application.port.input.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wefox.onboarding.server.ms.core.application.port.input.UpdateClaimUseCase.InputValues;
import com.wefox.onboarding.server.ms.core.application.port.input.UpdateClaimUseCase.InputValues.InputValuesBuilder;
import com.wefox.onboarding.server.ms.core.application.util.ClaimFactory;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;

public class UpdateClaimMapperTest {

  private final ClaimFactory claimFactory = new ClaimFactory();
  private final UpdateClaimMapper mapper = new UpdateClaimMapperImpl();

  @Test
  void Given_input_values_When_merged_toDomainEntity_Then_expected_equals_the_merged_entity() {
    long randomSuffix = RandomUtils.nextLong(1_000_000, 2_000_000);
    // Given
    var source = buildInputValues(randomSuffix).build();
    var merged = claimFactory.build();

    // When
    merged = mapper.merge(merged, source);

    // Then
    assertMerged(merged, source);
  }

  private void assertMerged(Claim actual, InputValues expected) {
    assertAll(
        () -> assertEquals("CLM_11111111", actual.getId(), "Id"),
        () -> assertEquals(expected.getEventDate(), actual.getEventDate(), "Event date"),
        () ->
            assertEquals(
                expected.getNotificationDate(), actual.getNotificationDate(), "Notification date"),
        () -> assertEquals(expected.getDescription(), actual.getDescription(), "Description"),
        () -> assertEquals(expected.getPlaceOfEvent(), actual.getPlaceOfEvent(), "Place of event"),
        () -> assertEquals("CTC_11111111", actual.getContractId().get(), "ContractId"),
        () -> assertEquals("ACT_11111111", actual.getAccountId(), "AccountId"),
        () -> assertEquals("CBG_11111111", actual.getCoverageId().get(), "CoverageId"),
        () -> assertEquals("PDT_11111111", actual.getProductId().get(), "ProductId"),
        () -> assertEquals("INS_11111111", actual.getInsuranceId().get(), "InsuranceId"),
        () -> assertEquals(expected.getType(), actual.getType(), "Type"),
        () -> assertTrue(actual.getWithoutCoverage(), "Without coverage"));
  }

  InputValuesBuilder buildInputValues(long randomSuffix) {
    return InputValues.builder()
        .claimId("CLM_" + randomSuffix)
        .eventDate(Optional.of(OffsetDateTime.now().minusMinutes(randomSuffix)))
        .notificationDate(OffsetDateTime.now().minusMinutes(randomSuffix / 2))
        .placeOfEvent(Optional.of("PlaceOfEvent" + randomSuffix))
        .description(Optional.of("Description" + randomSuffix))
        .type(String.valueOf(randomSuffix))
        .withoutCoverage(Boolean.TRUE);
  }
}
