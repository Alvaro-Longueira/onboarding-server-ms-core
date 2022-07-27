package com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract;

import com.wefox.onboarding.server.ms.core.application.port.output.ContractService;
import com.wefox.onboarding.server.ms.core.domain.entity.Claim;
import com.wefox.onboarding.server.ms.core.domain.entity.ClaimTypeSetting;
import com.wefox.onboarding.server.ms.core.domain.entity.Insurance;
import com.wefox.onboarding.server.ms.core.domain.enums.ErrorDetailsEnum;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.dto.contracts.ContractDto;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.dto.contracts.limits.ContractLimitDto;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.dto.contracts.limits.ContractLimitsDto;
import com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.contract.mapper.InsuranceMapper;
import com.wefox.server.lib.common.core.exception.BusinessException;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContractServiceImpl implements ContractService {

  private final ContractWebClient contractClient;
  private final InsuranceMapper insuranceMapper;

  @Override
  public Optional<Insurance> checkCoverage(Claim claim, ClaimTypeSetting claimTypeSetting) {
    var contractId =
        claim
            .getContractId()
            .orElseThrow(() -> new BusinessException(ErrorDetailsEnum.NULL_CONTRACT_ID));
    var eventDate =
        claim
            .getEventDate()
            .orElseThrow(() -> new BusinessException(ErrorDetailsEnum.NULL_EVENT_DATE));

    ResponseEntity<ContractLimitsDto> responseEntity =
        contractClient.getContractLimits(contractId, eventDate);
    if (HttpStatus.NOT_FOUND.equals(responseEntity.getStatusCode())) {
      throw new BusinessException(ErrorDetailsEnum.COVERAGE_CONTRACT_NOT_FOUND);
    }

    ContractLimitsDto contractLimits =
        Optional.ofNullable(responseEntity.getBody())
            .orElseThrow(() -> new BusinessException(ErrorDetailsEnum.COVERAGE_CONTRACT_NOT_FOUND));

    ContractLimitDto contractLimit = searchCoverage(contractLimits, claimTypeSetting);
    return retrieveContractData(contractId, eventDate, contractLimit);
  }

  private ContractLimitDto searchCoverage(
      ContractLimitsDto contractLimits, ClaimTypeSetting claimTypeSetting) {

    if (CollectionUtils.isEmpty(contractLimits.contractLimits())) {
      throw new BusinessException(ErrorDetailsEnum.COVERAGE_TYPE_NOT_COVERED);
    }

    return contractLimits.contractLimits().stream()
        .filter(
            contract ->
                contract.contractLimitType().equals(claimTypeSetting.getCoverageType())
                    && contract
                        .insuredEventClass()
                        .equalsIgnoreCase(claimTypeSetting.getInsuredEventClass()))
        .findFirst()
        .orElseThrow(() -> new BusinessException(ErrorDetailsEnum.COVERAGE_TYPE_NOT_COVERED));
  }

  private Optional<Insurance> retrieveContractData(
      String contractId, OffsetDateTime eventDate, ContractLimitDto contractLimit) {

    ResponseEntity<ContractDto> contractEntity = contractClient.getContract(contractId, eventDate);
    if (!HttpStatus.OK.equals(contractEntity.getStatusCode())) {
      throw new BusinessException(ErrorDetailsEnum.COVERAGE_CONTRACT_NOT_FOUND);
    }

    ContractDto contract =
        Optional.ofNullable(contractEntity.getBody())
            .orElseThrow(() -> new BusinessException(ErrorDetailsEnum.NULL_CONTRACT_BODY));

    Optional<Insurance> insurance =
        contract.insurances().stream()
            .filter(currentInsurance -> currentInsurance.id().equals(contractLimit.insuranceId()))
            .findFirst()
            .map(insuranceMapper::toDomainEntity);
    return insurance;
  }
}
