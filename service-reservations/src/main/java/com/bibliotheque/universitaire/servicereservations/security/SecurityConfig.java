package com.bibliotheque.universitaire.servicereservations.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Désactiver CSRF pour les API REST
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Activer CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/reservations/admin/**").hasRole("ADMIN") // Routes admin sécurisées
                        .requestMatchers("/api/reservations/{livreId}/**").hasRole("USER") // Routes utilisateur sécurisées
                        .requestMatchers("/api/reservations/**").permitAll() // Routes publiques
                        .anyRequest().authenticated() // Autres requêtes nécessitant une authentification
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        // Validation JWT
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_"); // Ajoute un préfixe ROLE_

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Initialiser les autorités avec celles extraites par défaut
            List<GrantedAuthority> authorities = new ArrayList<>(grantedAuthoritiesConverter.convert(jwt));

            // Extraire les rôles depuis realm_access.roles
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) realmAccess.get("roles");
                roles.forEach(role -> {
                    // Ajouter le préfixe uniquement si nécessaire
                    String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                    authorities.add(new SimpleGrantedAuthority(authority));
                    System.out.println("Rôle ajouté depuis realm_access.roles : " + authority);
                });
            }

            return authorities;
        });

        return jwtAuthenticationConverter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:4200"); // Autoriser votre frontend
        configuration.addAllowedMethod("*"); // Autoriser toutes les méthodes HTTP
        configuration.addAllowedHeader("*"); // Autoriser tous les en-têtes
        configuration.setAllowCredentials(true); // Autoriser les cookies ou les authentifications basées sur des sessions

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
