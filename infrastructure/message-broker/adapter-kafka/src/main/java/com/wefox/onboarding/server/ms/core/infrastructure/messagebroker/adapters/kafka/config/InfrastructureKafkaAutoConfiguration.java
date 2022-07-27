package com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka")
@Import(InfrastructureSchemaRegistryConfiguration.class)
public class InfrastructureKafkaAutoConfiguration {}
