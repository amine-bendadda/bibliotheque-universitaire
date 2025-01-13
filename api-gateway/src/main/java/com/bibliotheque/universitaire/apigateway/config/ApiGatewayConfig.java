package com.bibliotheque.universitaire.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class ApiGatewayConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                // Route vers le Service Livres
                .route("service-livres", r -> r.path("/api/livres/**")
                        .uri("lb://service-livres")) // Load Balancer Eureka vers service-livres

                // Route vers le Service Utilisateurs
                .route("service-utilisateurs", r -> r.path("/api/utilisateurs/**")
                        .uri("lb://service-utilisateurs")) // Load Balancer Eureka vers service-utilisateurs

                // Route vers le Service Emprunts
                .route("service-empruntes", r -> r.path("/api/emprunts/**")
                        .uri("lb://service-emprunts")) // Load Balancer Eureka vers service-emprunts

                .build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
