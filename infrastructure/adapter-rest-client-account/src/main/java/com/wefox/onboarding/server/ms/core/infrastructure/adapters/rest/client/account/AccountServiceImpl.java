package com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.account;

import com.wefox.onboarding.server.ms.core.application.port.output.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final AccountWebClient client;
  public static final String MISSING_ACCOUNT_ID = "MISSING_ACCOUNT_ID";

  @Override
  public boolean accountExists(String accountId) {
    if (MISSING_ACCOUNT_ID.equals(accountId)) {
      return true;
    } else {
      return HttpStatus.OK.equals(client.getAccount(accountId));
    }
  }
}
