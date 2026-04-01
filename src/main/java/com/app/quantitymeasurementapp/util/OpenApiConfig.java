package com.app.quantitymeasurementapp.util;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Adds a "Bearer Token" input box to every endpoint in Swagger UI.
 * After logging in via POST /api/v1/auth/login, paste the token
 * into the Authorize dialog and all subsequent requests will include it.
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title       = "Quantity Measurement API",
        version     = "1.0",
        description = "Compare, convert and perform arithmetic on physical quantities. " +
                      "Authenticate via JWT (email/password) or Google OAuth2."
    ),
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name   = "bearerAuth",
    type   = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiConfig {}
