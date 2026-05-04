package com.classicmodel.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI classicModelsOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Classic Models Order Inventory API")
                .description("Spring Data REST API for the Classic Models database — Capgemini Sprint April 2026")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Capgemini Sprint Team")
                    .email("team@classicmodel.com")));
    }
}
