package com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract;

import static com.wefox.onboarding.server.ms.core.domain.enums.ErrorDetailsEnum.COVERAGE_CONTRACT_NOT_FOUND;
import static com.wefox.onboarding.server.ms.core.domain.enums.ErrorDetailsEnum.COVERAGE_TYPE_NOT_COVERED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.domain.entity.ClaimTypeSetting;
import com.wefox.onboarding.server.ms.core.domain.entity.Insurance;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.dto.ContractFactory;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.dto.ContractLimitsFactory;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.dto.contracts.ContractDto;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.dto.contracts.limits.ContractLimitsDto;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.mapper.InsuranceMapper;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.mapper.InsuranceMapperImpl;
import com.wefox.server.lib.common.core.exception.BusinessException;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class ContractServiceImplTest {

  @Mock private ContractWebClient contractClient;
  @Spy private InsuranceMapper insuranceMapper = new InsuranceMapperImpl();

  @InjectMocks private ContractServiceImpl contractService;

  private final Claim claim = new Claim();
  private final ClaimTypeSetting claimTypeSetting = new ClaimTypeSetting();

  @BeforeEach
  void setup() {
    claim.setContractId(Optional.of("contractId"));
    claim.setEventDate(Optional.of(OffsetDateTime.now()));
    claimTypeSetting.setCoverageType("coverageType");
    claimTypeSetting.setInsuredEventClass("insuredEventClass");
  }

  @Test
  void successfulCheckCoverage() {
    givenContractLimitsAndContractExists();
    Optional<Insurance> insurance = contractService.checkCoverage(claim, claimTypeSetting);
    assertContractLimitsIsCalled();
    assertContractIsCalled();
    assertTrue(insurance.isPresent());
    assertEquals(String.valueOf(65269), insurance.get().id());
    assertEquals(String.valueOf(1101), insurance.get().productId());
    assertEquals("product description", insurance.get().productDescription());
  }

  @Test
  void unsuccessfulCheckCoverage_getContracts_returnsNull_throwsBusinessException() {
    givenContractLimitsExists();
    givenContractClient_getContracts_returnsNullBody();
    assertThrows(
        BusinessException.class,
        () -> contractService.checkCoverage(claim, claimTypeSetting),
        "throws BusinessException");
    assertContractLimitsIsCalled();
    assertContractIsCalled();
  }

  @Test
  void unsuccessfulCheckCoverage_getContractLimits_returnsNull_throwsBusinessException() {
    givenContractLimits_getContractLimits_returnsNullBody();
    assertThrows(
        BusinessException.class,
        () -> contractService.checkCoverage(claim, claimTypeSetting),
        "throws BusinessException");
    assertContractLimitsIsCalled();
  }

  @Test
  void invalidCoverageType() {
    givenContractLimitsExists();
    claimTypeSetting.setCoverageType("noExistingCoverageType");
    Executable executable = () -> contractService.checkCoverage(claim, claimTypeSetting);
    var exception = assertThrows(BusinessException.class, executable);
    var expectedCode = COVERAGE_TYPE_NOT_COVERED.getCode();
    assertEquals(expectedCode, exception.getCode());
    assertContractLimitsIsCalled();
    assertContractIsNotCalled();
  }

  @Test
  void invalidInsuredEventClass() {
    givenContractLimitsExists();
    claimTypeSetting.setInsuredEventClass("noExistingInsuredEventClass");
    Executable executable = () -> contractService.checkCoverage(claim, claimTypeSetting);
    var exception = assertThrows(BusinessException.class, executable);
    var expectedCode = COVERAGE_TYPE_NOT_COVERED.getCode();
    assertEquals(expectedCode, exception.getCode());
    assertContractLimitsIsCalled();
    assertContractIsNotCalled();
  }

  @Test
  void withoutCoverage() {
    givenContractLimitsEmpty();
    Executable executable = () -> contractService.checkCoverage(claim, claimTypeSetting);
    var exception = assertThrows(BusinessException.class, executable);
    var expectedCode = COVERAGE_TYPE_NOT_COVERED.getCode();
    assertEquals(expectedCode, exception.getCode());
    assertContractLimitsIsCalled();
    assertContractIsNotCalled();
  }

  @Test
  void notFoundContractLimits() {
    givenNotFoundContractLimits();
    Executable executable = () -> contractService.checkCoverage(claim, claimTypeSetting);
    var exception = assertThrows(BusinessException.class, executable);
    var expectedCode = COVERAGE_CONTRACT_NOT_FOUND.getCode();
    assertEquals(expectedCode, exception.getCode());
    assertContractLimitsIsCalled();
    assertContractIsNotCalled();
  }

  private void givenContractLimitsAndContractExists() {
    ContractLimitsDto contractLimits = new ContractLimitsFactory().build();
    ContractDto contracts = new ContractFactory().build();
    ResponseEntity<ContractLimitsDto> responseEntity =
        new ResponseEntity(contractLimits, HttpStatus.OK);
    when(contractClient.getContractLimits(any(), any())).thenReturn(responseEntity);
    ResponseEntity<ContractDto> contractDtoResponseEntity =
        new ResponseEntity<>(contracts, HttpStatus.OK);
    when(contractClient.getContract(any(), any())).thenReturn(contractDtoResponseEntity);
  }

  private void givenContractLimitsExists() {
    ContractLimitsDto contractLimits = new ContractLimitsFactory().build();
    ContractDto contracts = new ContractFactory().build();
    ResponseEntity<ContractLimitsDto> responseEntity =
        new ResponseEntity(contractLimits, HttpStatus.OK);
    when(contractClient.getContractLimits(any(), any())).thenReturn(responseEntity);
    ResponseEntity<ContractDto> contractDtoResponseEntity =
        new ResponseEntity<>(contracts, HttpStatus.OK);
    when(contractClient.getContract(any(), any())).thenReturn(contractDtoResponseEntity);
  }

  private void givenContractClient_getContracts_returnsNullBody() {
    when(contractClient.getContract(any(), any())).thenReturn(ResponseEntity.ok(null));
  }

  private void givenContractLimits_getContractLimits_returnsNullBody() {
    when(contractClient.getContractLimits(any(), any())).thenReturn(ResponseEntity.ok(null));
  }

  private void givenContractLimitsEmpty() {
    ContractLimitsDto contractLimits = new ContractLimitsDto();
    ResponseEntity<ContractLimitsDto> responseEntity =
        new ResponseEntity(contractLimits, HttpStatus.OK);
    when(contractClient.getContractLimits(any(), any())).thenReturn(responseEntity);
  }

  private void givenNotFoundContractLimits() {
    ResponseEntity<ContractLimitsDto> responseEntity = new ResponseEntity(HttpStatus.NOT_FOUND);
    when(contractClient.getContractLimits(any(), any())).thenReturn(responseEntity);
  }

  private void assertContractLimitsIsCalled() {
    verify(contractClient, times(1))
        .getContractLimits(claim.getContractId().get(), claim.getEventDate().get());
  }

  private void assertContractIsCalled() {
    verify(contractClient, times(1))
        .getContract(claim.getContractId().get(), claim.getEventDate().get());
  }

  private void assertContractIsNotCalled() {
    verify(contractClient, times(0)).getContract(any(), any());
  }
}
