//package com.bibliotheque.universitaire.servicelivres.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
//import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
//import org.springframework.security.web.SecurityFilterChain;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Configuration
//@EnableMethodSecurity(prePostEnabled = true)
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // Désactiver CSRF pour les API REST
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/livres/public/**").permitAll() // Routes publiques
//                        .requestMatchers("/api/livres/{id}/disponible").permitAll()
//                        .requestMatchers("/api/livres/{id}/indisponible").permitAll()
//                        .requestMatchers("/api/livres").hasRole("ADMIN") // POST sur /api/livres nécessite ADMIN
//                        .requestMatchers("/api/livres/**").hasAnyRole("USER", "ADMIN") // Routes pour GET / PUT
//                        .requestMatchers("/api/livres/categories").hasRole("ADMIN")
//                        .anyRequest().authenticated() // Toute autre requête nécessite une authentification
//                )
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))); // Activer la validation des JWT
//        return http.build();
//    }
//
//    @Bean
//    public JwtAuthenticationConverter jwtAuthenticationConverter() {
//        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_"); // Ajoute un préfixe ROLE_
//
//        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
//        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
//            // Initialiser les autorités avec celles extraites par défaut
//            List<GrantedAuthority> authorities = new ArrayList<>(grantedAuthoritiesConverter.convert(jwt));
//
//            // Extraire les rôles depuis realm_access.roles
//            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
//            if (realmAccess != null && realmAccess.containsKey("roles")) {
//                @SuppressWarnings("unchecked")
//                List<String> roles = (List<String>) realmAccess.get("roles");
//                roles.forEach(role -> {
//                    // Ajouter le préfixe uniquement si nécessaire
//                    String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
//                    authorities.add(new SimpleGrantedAuthority(authority));
//                    System.out.println("Rôle ajouté depuis realm_access.roles : " + authority);
//                });
//            }
//
//            return authorities;
//        });
//
//        return jwtAuthenticationConverter;
//    }
//
//}

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Ajouter la configuration CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/livres/public/**").permitAll() // Routes publiques
                        .requestMatchers("/api/livres/{id}/disponible").permitAll()
                        .requestMatchers("/api/livres/{id}/indisponible").permitAll()
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

    // Configuration CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:4200"); // Origine autorisée
        configuration.addAllowedMethod("*"); // Toutes les méthodes (GET, POST, PUT, DELETE)
        configuration.addAllowedHeader("*"); // Tous les en-têtes
        configuration.setAllowCredentials(true); // Autoriser l'envoi des cookies/tokens

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Appliquer à toutes les routes
        return source;
    }
}
