package com.soramitus.assignment.api_gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/auth/**")
                        .filters(f -> f.rewritePath("/api/auth/(?<segment>.*)", "/${segment}"))
                        .uri("lb://AUTH-SERVICE"))
                .route("wallet-service", r -> r.path("/api/v1/wallets/**")
                        .filters(f -> f.filter(jwtAuthFilter).rewritePath("/api/wallets/(?<segment>.*)", "/${segment}"))
                        .uri("lb://WALLET-SERVICE"))
                .route("gift-code-service", r -> r.path("/api/v1/gift-codes/**")
                        .filters(f -> f.rewritePath("/api/gift-codes/(?<segment>.*)", "/${segment}"))
                        .uri("lb://GIFT-CODE-SERVICE"))
                .route("transaction-service", r -> r.path("/api/v1/transactions/**")
                        .filters(f -> f.rewritePath("/api/v1/transactions/(?<segment>.*)", "/${segment}"))
                        .uri("lb://transaction-service"))
                .route("exchange-rate-service", r -> r.path("/api/v1/exchange-rates/**")
                        .filters(f -> f.rewritePath("/api/v1/exchange-rates/(?<segment>.*)", "/${segment}"))
                        .uri("lb://exchange-rate-service"))
                .build();

    }

}
