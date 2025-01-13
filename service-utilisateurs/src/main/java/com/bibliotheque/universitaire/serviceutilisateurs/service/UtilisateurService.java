//package com.bibliotheque.universitaire.serviceutilisateurs.service;
//
//import com.bibliotheque.universitaire.serviceutilisateurs.model.Utilisateur;
//import com.bibliotheque.universitaire.serviceutilisateurs.model.Role;
//import com.bibliotheque.universitaire.serviceutilisateurs.repository.UtilisateurRepository;
//import com.bibliotheque.universitaire.serviceutilisateurs.repository.RoleRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class UtilisateurService {
//
//    @Autowired
//    private UtilisateurRepository utilisateurRepository;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    // URL et configurations Keycloak
//    private final String keycloakServerUrl = "http://localhost:8080";
//    private final String keycloakRealm = "bibliotheque";
//    private final String keycloakClientId = "admin-cli-bibliotheque"; // Doit être dans le realm bibliotheque
//    private final String keycloakAdminUsername = "admin"; // Compte d'administration
//    private final String keycloakAdminPassword = "admin"; // Mot de passe
//
//    public Utilisateur inscription(Utilisateur utilisateur) {
//        // Encodage du mot de passe
//        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
//
//        // Ajouter le rôle par défaut ROLE_USER
//        Role roleUtilisateur = roleRepository.findByNom("ROLE_USER");
//        utilisateur.getRoles().add(roleUtilisateur);
//
//        // Créer l'utilisateur dans la base de données
//        Utilisateur nouvelUtilisateur = utilisateurRepository.save(utilisateur);
//
//        // Synchroniser avec Keycloak
//        creerUtilisateurDansKeycloak(nouvelUtilisateur);
//
//        return nouvelUtilisateur;
//    }
//
//    public Optional<Utilisateur> login(String email) {
//        return utilisateurRepository.findByEmail(email);
//    }
//
//    public List<Utilisateur> recupererTousLesUtilisateurs() {
//        return utilisateurRepository.findAll();
//    }
//
//    public Utilisateur modifierUtilisateur(Long id, Utilisateur utilisateurDetails) {
//        Utilisateur utilisateur = utilisateurRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
//
//        utilisateur.setNom(utilisateurDetails.getNom());
//        utilisateur.setPrenom(utilisateurDetails.getPrenom());
//        utilisateur.setEmail(utilisateurDetails.getEmail());
//
//        return utilisateurRepository.save(utilisateur);
//    }
//
//    private String obtenirTokenAdmin() {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        Map<String, String> body = new HashMap<>();
//        body.put("grant_type", "password");
//        body.put("client_id", keycloakClientId);
//        body.put("username", keycloakAdminUsername);
//        body.put("password", keycloakAdminPassword);
//
//        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
//
//        String tokenUrl = keycloakServerUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/token";
//        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, entity, Map.class);
//
//        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
//            throw new RuntimeException("Impossible d'obtenir un token admin depuis Keycloak.");
//        }
//
//        return (String) response.getBody().get("access_token");
//    }
//
//    private void creerUtilisateurDansKeycloak(Utilisateur utilisateur) {
//        String token = obtenirTokenAdmin();
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(token);
//
//        Map<String, Object> request = Map.of(
//                "username", utilisateur.getEmail(),
//                "email", utilisateur.getEmail(),
//                "enabled", true,
//                "credentials", List.of(Map.of("type", "password", "value", utilisateur.getMotDePasse(), "temporary", false))
//        );
//
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
//
//        ResponseEntity<String> response = restTemplate.postForEntity(
//                keycloakServerUrl + "/admin/realms/" + keycloakRealm + "/users",
//                entity,
//                String.class
//        );
//
//        if (!response.getStatusCode().is2xxSuccessful()) {
//            throw new RuntimeException("Erreur lors de la création de l'utilisateur dans Keycloak : " + response.getBody());
//        }
//
//        // Attribuer des rôles après la création
//        attribuerRolesDansKeycloak(utilisateur.getEmail(), utilisateur.getRoles());
//    }
//
//
//
//    private void attribuerRolesDansKeycloak(String email, List<Role> roles) {
//        String token = obtenirTokenAdmin();
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        // Préparer les headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(token);
//
//        HttpEntity<Void> entity = new HttpEntity<>(headers);
//
//        // Récupérer l'utilisateur par email
//        ResponseEntity<Map[]> response = restTemplate.exchange(
//                keycloakServerUrl + "/admin/realms/" + keycloakRealm + "/users?username=" + email,
//                HttpMethod.GET,
//                entity,
//                Map[].class
//        );
//
//        if (response.getBody() == null || response.getBody().length == 0) {
//            throw new RuntimeException("Utilisateur non trouvé dans Keycloak");
//        }
//
//        String userId = (String) response.getBody()[0].get("id");
//
//        // Ajouter les rôles au client `service-livres-client`
//        roles.forEach(role -> {
//            Map<String, Object> roleRepresentation = Map.of(
//                    "name", role.getNom()
//            );
//
//            HttpEntity<Map<String, Object>> roleEntity = new HttpEntity<>(roleRepresentation, headers);
//
//            restTemplate.postForEntity(
//                    keycloakServerUrl + "/admin/realms/" + keycloakRealm + "/users/" + userId + "/role-mappings/clients/" + getClientId(),
//                    roleEntity,
//                    String.class
//            );
//        });
//    }
//
//    // Méthode pour récupérer l'ID du client service-livres-client
//    private String getClientId() {
//        String token = obtenirTokenAdmin();
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        // Préparer les headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(token);
//
//        HttpEntity<Void> entity = new HttpEntity<>(headers);
//
//        // Récupérer les clients pour le realm `bibliotheque`
//        ResponseEntity<Map[]> response = restTemplate.exchange(
//                keycloakServerUrl + "/admin/realms/" + keycloakRealm + "/clients",
//                HttpMethod.GET,
//                entity,
//                Map[].class
//        );
//
//        if (response.getBody() != null) {
//            for (Map<String, Object> client : response.getBody()) {
//                if ("service-livres-client".equals(client.get("clientId"))) {
//                    return (String) client.get("id");
//                }
//            }
//        }
//
//        throw new RuntimeException("Client `service-livres-client` non trouvé dans Keycloak");
//    }
//
//
//
//
//}
