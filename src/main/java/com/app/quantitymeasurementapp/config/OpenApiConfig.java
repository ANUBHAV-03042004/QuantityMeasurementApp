package com.app.quantitymeasurementapp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configures Swagger UI to show a padlock on secured endpoints and allow
 * testers to paste their JWT token directly in the UI.
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title       = "Quantity Measurement API",
        version     = "2.0",
        description = "Compare, convert and perform arithmetic on physical quantities. " +
                      "Register → Login → copy token → click Authorize 🔒"
    ),
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name         = "bearerAuth",
    type         = SecuritySchemeType.HTTP,
    scheme       = "bearer",
    bearerFormat = "JWT",
    in           = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {}
