package com.wefox.onboarding.server.ms.core.infrastructure.adapters.rest.client.account;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class AccountServiceImplTest {

  @Mock private AccountWebClient client;

  @InjectMocks private AccountServiceImpl accountService;

  @Test
  void givenValidAccountId_whenAccountExists_ReturnsTrue() {
    String accountId = "accountId";
    when(client.getAccount(accountId)).thenReturn(HttpStatus.OK);

    boolean result = accountService.accountExists("accountId");
    verify(client, times(1)).getAccount(accountId);
    Assertions.assertTrue(result);
  }

  @Test
  void givenInvalidAccountId_whenAccountExists_ReturnsFalse() {
    String accountId = "accountId";
    when(client.getAccount(accountId)).thenReturn(HttpStatus.NOT_FOUND);

    boolean result = accountService.accountExists("accountId");
    verify(client, times(1)).getAccount(accountId);
    Assertions.assertFalse(result);
  }

  @Test
  void givenMissingAccountId_whenAccountExists_ReturnsTrue() {
    boolean result = accountService.accountExists(AccountServiceImpl.MISSING_ACCOUNT_ID);
    verify(client, never()).getAccount(any());
    Assertions.assertTrue(result);
  }
}
