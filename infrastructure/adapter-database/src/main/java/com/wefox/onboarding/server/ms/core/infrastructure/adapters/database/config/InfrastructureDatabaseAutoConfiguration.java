package com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@Configuration
@ComponentScan("com.wefox.onboarding.server.ms.core.infrastructure.adapters.database")
@EntityScan("com.wefox.onboarding.server.ms.core.infrastructure.adapters.database")
@EnableJdbcRepositories("com.wefox.onboarding.server.ms.core.infrastructure.adapters.database")
public class InfrastructureDatabaseAutoConfiguration {}
