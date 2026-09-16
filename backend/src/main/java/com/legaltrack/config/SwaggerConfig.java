package com.legaltrack.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                )
                .info(new Info()
                        .title("LEGALTRACK API Documentation")
                        .version("1.0.0")
                        .description("REST API documentation for LegalTrack - Legal Case Tracking, Lawyer Discovery, Legal-Aid Assistance & Client-Lawyer Management Portal")
                        .contact(new Contact()
                                .name("LegalTrack Architecture Team")
                                .email("support@example.com")
                        )
                        .license(new License()
                                .name("Academic License")
                                .url("https://github.com/varunesh6/LegalTracker")
                        )
                );
    }
}
