package com.bibliotheque.universitaire.serviceutilisateurs.service;

import com.bibliotheque.universitaire.serviceutilisateurs.model.Utilisateur;
import com.bibliotheque.universitaire.serviceutilisateurs.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Utilisateur inscrireUtilisateur(Utilisateur utilisateur) {
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        return utilisateurRepository.save(utilisateur);
    }

    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

}
