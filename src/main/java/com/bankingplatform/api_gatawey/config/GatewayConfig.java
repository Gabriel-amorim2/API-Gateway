package com.bankingplatform.api_gatawey.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("auth-service", r -> r.path("/auth/**")
                        .uri("http://localhost:8081"))

                .route("accounts-service", r -> r.path("/accounts/**", "/transfers/**")
                        .uri("http://localhost:8082"))

                .route("cards-service", r -> r.path("/cards/**")
                        .uri("http://localhost:8083"))

                .build();
    }
}


