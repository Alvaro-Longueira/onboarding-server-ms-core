package com.wefox.onboarding.server.ms.core.main.test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgreSqlContainer {

  protected static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
      new PostgreSQLContainer<>("postgres:12.4-alpine")
          .withDatabaseName("claim-it-db")
          .withUsername("claim-it-db-user")
          .withPassword("claim-it-db-password")
          .withReuse(true);

  static {
    CompletableFuture<Void> future = CompletableFuture.runAsync(POSTGRESQL_CONTAINER::start);

    try {
      future.get();
    } catch (InterruptedException | ExecutionException exception) {
      throw new RuntimeException("Exception starting integration-test containers.", exception);
    }
  }

  @DynamicPropertySource
  private static void registerPgProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
    registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
  }
}
