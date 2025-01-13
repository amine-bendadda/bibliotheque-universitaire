package com.bibliotheque.universitaire.serviceutilisateurs.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak.admin-client-id}")
    private String keycloakAdminClientId;

    @Value("${keycloak.admin-username}")
    private String keycloakAdminUsername;

    @Value("${keycloak.admin-password}")
    private String keycloakAdminPassword;

    private final RestTemplate restTemplate = new RestTemplate();

    // Obtenir le token d'administration Keycloak
    private String obtenirTokenAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "client_id=" + keycloakAdminClientId +
                "&username=" + keycloakAdminUsername +
                "&password=" + keycloakAdminPassword +
                "&grant_type=password";

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                keycloakServerUrl + "/realms/master/protocol/openid-connect/token",
                request,
                Map.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Erreur lors de l'obtention du token d'administration Keycloak");
        }

        return (String) response.getBody().get("access_token");
    }

    // 1. Login utilisateur connecté
    @PostMapping("/login")
    public Map<String, Object> login(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return Map.of(
                "username", jwt.getClaimAsString("preferred_username"),
                "email", jwt.getClaimAsString("email"),
                "roles", jwt.getClaimAsMap("realm_access").get("roles")
        );
    }

    // 2. Inscription utilisateur
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> user) {
        String token = obtenirTokenAdmin();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> request = Map.of(
                "username", user.get("username"),
                "email", user.get("email"),
                "enabled", true,
                "credentials", List.of(Map.of("type", "password", "value", user.get("password"), "temporary", false))
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                keycloakServerUrl + "/admin/realms/" + keycloakRealm + "/users",
                entity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(response.getStatusCode()).body("Erreur lors de l'inscription utilisateur : " + response.getBody());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Utilisateur inscrit avec succès");
    }

    // 3. Afficher les informations de l'utilisateur connecté
    @GetMapping("/me")
    public Map<String, Object> getUserInfo(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return Map.of(
                "username", jwt.getClaimAsString("preferred_username"),
                "email", jwt.getClaimAsString("email"),
                "roles", jwt.getClaimAsMap("realm_access").get("roles")
        );
    }

    // 4. Afficher tous les utilisateurs
    @GetMapping("/all")
    public ResponseEntity<Object> getAllUsers() {
        String token = obtenirTokenAdmin();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Object> response = restTemplate.exchange(
                keycloakServerUrl + "/admin/realms/" + keycloakRealm + "/users",
                HttpMethod.GET,
                entity,
                Object.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(response.getStatusCode()).body("Erreur lors de la récupération des utilisateurs : " + response.getBody());
        }

        return ResponseEntity.ok(response.getBody());
    }

    // 5. Modifier les informations de l'utilisateur connecté
    @PutMapping("/me")
    public ResponseEntity<String> updateUser(Authentication authentication, @RequestBody Map<String, Object> updates) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userId = jwt.getClaimAsString("sub");
        String token = obtenirTokenAdmin();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(updates, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                keycloakServerUrl + "/admin/realms/" + keycloakRealm + "/users/" + userId,
                HttpMethod.PUT,
                entity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(response.getStatusCode()).body("Erreur lors de la mise à jour des informations utilisateur : " + response.getBody());
        }

        return ResponseEntity.ok("Informations utilisateur mises à jour avec succès");
    }

    // 6. Désactiver un compte utilisateur
    @DeleteMapping("/{id}/disable")
    public ResponseEntity<String> disableUser(@PathVariable String id) {
        String token = obtenirTokenAdmin();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(Map.of("enabled", false), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                keycloakServerUrl + "/admin/realms/" + keycloakRealm + "/users/" + id,
                HttpMethod.PUT,
                entity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(response.getStatusCode()).body("Erreur lors de la désactivation de l'utilisateur : " + response.getBody());
        }

        return ResponseEntity.ok("Utilisateur désactivé avec succès");
    }

    // 7. Afficher les informations d'un utilisateur spécifique
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserDetails(@PathVariable String id) {
        String token = obtenirTokenAdmin();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Object> response = restTemplate.exchange(
                keycloakServerUrl + "/admin/realms/" + keycloakRealm + "/users/" + id,
                HttpMethod.GET,
                entity,
                Object.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(response.getStatusCode()).body("Erreur lors de la récupération des informations utilisateur : " + response.getBody());
        }

        return ResponseEntity.ok(response.getBody());
    }
}
