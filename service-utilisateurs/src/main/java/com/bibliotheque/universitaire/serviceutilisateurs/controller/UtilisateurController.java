package com.bibliotheque.universitaire.serviceutilisateurs.controller;

import com.bibliotheque.universitaire.serviceutilisateurs.model.Utilisateur;
import com.bibliotheque.universitaire.serviceutilisateurs.repository.UtilisateurRepository;
import com.bibliotheque.universitaire.serviceutilisateurs.security.JwtTokenProvider;
import com.bibliotheque.universitaire.serviceutilisateurs.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/auth/inscription")
    public ResponseEntity<Utilisateur> inscrireUtilisateur(@RequestBody Utilisateur utilisateur) {
        Utilisateur nouvelUtilisateur = utilisateurService.inscrireUtilisateur(utilisateur);
        return ResponseEntity.ok(nouvelUtilisateur);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestBody Utilisateur utilisateur) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(utilisateur.getEmail(), utilisateur.getMotDePasse())
        );

        org.springframework.security.core.userdetails.User userDetails =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        String[] roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);

        String token = jwtTokenProvider.generateToken(userDetails.getUsername(), roles);

        return ResponseEntity.ok(token);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Utilisateur>> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        return ResponseEntity.ok(utilisateurs);
    }

    // Modifier les informations d'un utilisateur (seulement par le propriétaire)
    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> updateUtilisateur(@PathVariable Long id, @RequestBody Utilisateur utilisateurDetails) {
        // Obtenez l'utilisateur connecté
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailConnecte = authentication.getName();

        // Vérifiez si l'utilisateur connecté est propriétaire du compte
        Optional<Utilisateur> optionalUtilisateur = utilisateurRepository.findById(id);
        if (optionalUtilisateur.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Utilisateur utilisateur = optionalUtilisateur.get();
        if (!utilisateur.getEmail().equals(emailConnecte)) {
            return ResponseEntity.status(403).build(); // Forbidden si l'utilisateur connecté n'est pas le propriétaire
        }

        // Mise à jour des informations
        utilisateur.setNom(utilisateurDetails.getNom());
        utilisateur.setEmail(utilisateurDetails.getEmail());

        // Si le mot de passe est modifié, hachez-le avant de le sauvegarder
        if (utilisateurDetails.getMotDePasse() != null && !utilisateurDetails.getMotDePasse().isEmpty()) {
            utilisateur.setMotDePasse(utilisateurService.encodePassword(utilisateurDetails.getMotDePasse()));
        }

        Utilisateur utilisateurMisAJour = utilisateurRepository.save(utilisateur);
        return ResponseEntity.ok(utilisateurMisAJour);
    }
}
