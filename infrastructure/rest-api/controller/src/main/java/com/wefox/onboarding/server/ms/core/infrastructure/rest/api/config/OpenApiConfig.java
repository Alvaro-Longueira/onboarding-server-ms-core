package com.wefox.onboarding.server.ms.core.infrastructure.rest.api.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@SecurityScheme(
    name = "wefox_id",
    type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(clientCredentials = @OAuthFlow(tokenUrl = "${springdoc.oauth.token-url}")))
@Configuration
public class OpenApiConfig {}
