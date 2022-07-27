package com.wefox.onboarding.server.ms.core.application.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.wefox.onboarding.server.ms.core.application.config.ApplicationConfigProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApplicationUtilsTest {

  @Mock private ApplicationConfigProperties properties;

  @InjectMocks private ApplicationUtils utils;

  @Test
  void skipSymassEnabled() {
    when(properties.getMarketId()).thenReturn("it");
    when(properties.getSymassSkip()).thenReturn("it");
    assertTrue(utils.skipSymass());
  }

  @Test
  void skipSymassDisabled() {
    when(properties.getMarketId()).thenReturn("de");
    when(properties.getSymassSkip()).thenReturn("it");
    assertFalse(utils.skipSymass());
  }

  @Test
  void noSendEventsEnabled() {
    when(properties.getMarketId()).thenReturn("it");
    when(properties.getNoSendEvents()).thenReturn("it");
    assertTrue(utils.noSendEvents());
  }

  @Test
  void noSendEventsDisabled() {
    when(properties.getMarketId()).thenReturn("de");
    when(properties.getNoSendEvents()).thenReturn("it");
    assertFalse(utils.noSendEvents());
  }
}
