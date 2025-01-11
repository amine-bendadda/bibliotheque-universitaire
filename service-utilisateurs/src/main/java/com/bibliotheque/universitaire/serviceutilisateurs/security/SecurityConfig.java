package com.bibliotheque.universitaire.serviceutilisateurs.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Désactiver CSRF pour simplifier les tests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/utilisateurs/auth/**").permitAll() // Routes publiques
                        .requestMatchers("/api/utilisateurs/all").hasRole("ADMIN") // Routes réservées aux admins
                        .anyRequest().authenticated() // Authentification requise pour le reste
                )
                .httpBasic(customizer -> {}); // Activer HTTP Basic sans utiliser httpBasic()

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Encoder des mots de passe avec BCrypt
    }
}
