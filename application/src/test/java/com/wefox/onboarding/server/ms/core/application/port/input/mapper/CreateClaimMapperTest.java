package com.wefox.onboarding.server.ms.core.application.port.input.mapper;

import static com.wefox.server.lib.common.test.Assertions.assertSamePropertyValues;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wefox.onboarding.server.ms.core.application.util.ClaimFactory;
import com.wefox.onboarding.server.ms.core.application.util.CreateClaimInputValuesFactory;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CreateClaimMapperTest {

  private final CreateClaimInputValuesFactory createClaimInputValuesFactory =
      new CreateClaimInputValuesFactory();
  private final ClaimFactory claimFactory = new ClaimFactory();

  private final CreateClaimMapper mapper = new CreateClaimMapperImpl();

  @Test
  void Given_a_input_values_When_mapped_toDomainEntity_Then_expected_equals_mapped() {
    // Given
    var original = createClaimInputValuesFactory.build();
    // make sure entry date is set to null as this field is not present in Input values
    var expected =
        claimFactory.build(
            c -> {
              c.setInsuranceId(Optional.empty());
              c.setCoverageId(Optional.empty());
              c.setEntryDate(null);
            });

    // When
    var mapped = mapper.toDomainEntity(original);

    // Then
    // check equals method
    assertEquals(expected, mapped, "equals");
    // check hashCode method
    assertEquals(expected.hashCode(), mapped.hashCode(), "hashCode");
    // check same property values
    assertSamePropertyValues(Claim.class, expected, mapped);
  }
}
