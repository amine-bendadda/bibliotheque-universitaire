package com.bibliotheque.universitaire.servicelivres.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Désactiver CSRF pour les API REST
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/livres/public/**").permitAll() // Routes publiques
                        .requestMatchers("/api/livres").hasRole("ADMIN") // POST sur /api/livres nécessite ADMIN
                        .requestMatchers("/api/livres/**").hasAnyRole("USER", "ADMIN") // Routes pour GET / PUT
                        .requestMatchers("/api/livres/categories").hasRole("ADMIN")
                        .anyRequest().authenticated() // Toute autre requête nécessite une authentification
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))); // Activer la validation des JWT
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("resource_access.service-livres-client.roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Initialiser une collection vide de GrantedAuthority
            List<GrantedAuthority> authorities = new ArrayList<>(grantedAuthoritiesConverter.convert(jwt));

            // Extraire les rôles de resource_access.service-livres-client.roles
            Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
            if (resourceAccess != null && resourceAccess.containsKey("service-livres-client")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> serviceLivresRoles = (Map<String, Object>) resourceAccess.get("service-livres-client");
                if (serviceLivresRoles.containsKey("roles")) {
                    @SuppressWarnings("unchecked")
                    List<String> roles = (List<String>) serviceLivresRoles.get("roles");
                    roles.forEach(role -> {
                        authorities.add(new SimpleGrantedAuthority(role));
                        System.out.println("Role ajouté depuis resource_access.service-livres-client : " + role);
                    });
                }
            }

            return authorities;
        });

        return jwtAuthenticationConverter;
    }
}
