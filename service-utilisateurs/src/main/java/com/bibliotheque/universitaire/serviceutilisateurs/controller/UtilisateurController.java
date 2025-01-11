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
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


}
