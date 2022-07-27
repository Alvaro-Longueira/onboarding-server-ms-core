package com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.mapper;

import static com.wefox.server.lib.common.test.Assertions.assertSamePropertyValues;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wefox.onboarding.server.ms.core.domain.entity.Insurance;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.dto.ContractFactory;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.dto.InsuranceFactory;
import org.junit.jupiter.api.Test;

class InsuranceMapperTest {

  private final ContractFactory contractFactory = new ContractFactory();
  private final InsuranceFactory insuranceFactory = new InsuranceFactory();
  private final InsuranceMapper mapper = new InsuranceMapperImpl();

  @Test
  void Given_a_input_values_When_mapped_toDomainEntity_Then_expected_equals_mapped() {
    var original = contractFactory.build();
    var expected = insuranceFactory.build();

    var mapped = mapper.toDomainEntity(original.insurances().get(0));

    assertEquals(expected, mapped, "equals");
    assertEquals(expected.hashCode(), mapped.hashCode(), "hashCode");
    assertSamePropertyValues(Insurance.class, expected, mapped);
  }
}
