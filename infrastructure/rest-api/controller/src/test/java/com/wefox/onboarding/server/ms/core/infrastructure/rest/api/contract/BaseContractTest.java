package com.wefox.onboarding.server.ms.core.infrastructure.rest.api.contract;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.wefox.onboarding.server.ms.core.application.port.input.CreateClaimUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.FindAllClaimsUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.GetClaimByIdUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.UpdateClaimStatusUseCase;
import com.wefox.onboarding.server.ms.core.application.port.input.UpdateClaimUseCase;
import com.wefox.onboarding.server.ms.core.application.service.ClaimService;
import com.wefox.server.lib.common.web.mvc.config.WebMvcAutoConfiguration;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@SpringBootTest(
    classes = TestApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({WebMvcAutoConfiguration.class, ErrorMvcAutoConfiguration.class})
@Slf4j
public abstract class BaseContractTest {

  @LocalServerPort int port;

  @Autowired MockBackend backend;

  @MockBean CreateClaimUseCase createClaimUseCase;
  @MockBean FindAllClaimsUseCase findAllClaimsUseCase;
  @MockBean GetClaimByIdUseCase getClaimByIdUseCase;
  @MockBean UpdateClaimStatusUseCase updateClaimStatusUseCase;
  @MockBean UpdateClaimUseCase updateClaimUseCase;

  @MockBean ClaimService claimService;

  @BeforeEach
  public void setUp() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = port;

    when(createClaimUseCase.execute(any()))
        .thenAnswer(
            c -> backend.createClaimUseCase((CreateClaimUseCase.InputValues) c.getArguments()[0]));
    when(findAllClaimsUseCase.execute(any())).thenAnswer(c -> backend.findAllClaimsUseCase());
    when(getClaimByIdUseCase.execute(any()))
        .thenAnswer(
            c ->
                backend.getClaimByIdUseCase((GetClaimByIdUseCase.InputValues) c.getArguments()[0]));
    when(updateClaimStatusUseCase.execute(any()))
        .thenAnswer(
            c ->
                backend.updateClaimStatusUseCase(
                    (UpdateClaimStatusUseCase.InputValues) c.getArguments()[0]));
    when(updateClaimUseCase.execute(any()))
        .thenAnswer(
            c -> backend.updateClaimUseCase((UpdateClaimUseCase.InputValues) c.getArguments()[0]));
  }
}
