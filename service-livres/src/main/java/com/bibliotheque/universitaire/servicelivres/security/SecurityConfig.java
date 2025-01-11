package com.bibliotheque.universitaire.servicelivres.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    private static final String SECRET_KEY = "Q2hzVTVRT0k2UW1NUHlRV1RQZlA1UmQyYWV3enNwWTVvVXZNeG5ZYnVMTVpmVzFMRHpNUHlR";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Désactiver CSRF pour les APIs REST
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/livres/public/**").permitAll() // Routes publiques
                        .requestMatchers("/api/livres/**").hasRole("ADMIN") // Routes protégées (Admin uniquement)
                        .anyRequest().authenticated() // Toute autre route nécessite une authentification
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder()))); // Activer la validation des tokens JWT

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

}
