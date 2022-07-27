package com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.schema.registry.client.ConfluentSchemaRegistryClient;
import org.springframework.cloud.schema.registry.client.EnableSchemaRegistryClient;
import org.springframework.cloud.schema.registry.client.SchemaRegistryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableSchemaRegistryClient
public class InfrastructureSchemaRegistryConfiguration {

  @Bean
  @ConditionalOnMissingBean(SchemaRegistryClient.class)
  public SchemaRegistryClient schemaRegistryClient(
      @Value("${spring.cloud.stream.kafka.binder.producer-properties.schema.registry.url}")
          String endpoint) {
    ConfluentSchemaRegistryClient client = new ConfluentSchemaRegistryClient();
    client.setEndpoint(endpoint);
    return client;
  }
}
