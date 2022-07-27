package com.wefox.onboarding.server.ms.core.application.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("com.wefox.onboarding.server.ms.core.application")
@Import(ApplicationConfigProperties.class)
public class ApplicationAutoConfiguration {}
