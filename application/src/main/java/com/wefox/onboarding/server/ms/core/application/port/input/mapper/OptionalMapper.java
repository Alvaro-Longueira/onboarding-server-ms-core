package com.wefox.onboarding.server.ms.core.application.port.input.mapper;

import java.util.Optional;

public class OptionalMapper {

  public static <T> Optional<T> wrapOptional(T object) {
    return Optional.ofNullable(object);
  }

  public static <T> T getOptional(Optional<T> object) {
    return object.orElse(null);
  }
}
