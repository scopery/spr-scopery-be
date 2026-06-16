package com.company.scopery.platform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI scoperyOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Scopery API")
                        .description("Backend API for Scopery application, including AI Agent configuration module.")
                        .version("v1"))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Local development server"));
    }
}