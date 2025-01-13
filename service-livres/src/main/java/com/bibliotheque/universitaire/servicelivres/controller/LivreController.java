package com.bibliotheque.universitaire.servicelivres.controller;

import com.bibliotheque.universitaire.servicelivres.model.Livre;
import com.bibliotheque.universitaire.servicelivres.model.Categorie;
import com.bibliotheque.universitaire.servicelivres.repository.LivreRepository;
import com.bibliotheque.universitaire.servicelivres.repository.CategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/livres")
public class LivreController {

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    // Récupérer tous les livres (accessible à tous les utilisateurs authentifiés)
    @GetMapping("/public")
    public ResponseEntity<List<Livre>> getAllLivres() {
        return ResponseEntity.ok(livreRepository.findAll());
    }

    // Récupérer un livre par ID (accessible à tous les utilisateurs authentifiés)
    @GetMapping("/public/{id}")
    public ResponseEntity<Livre> getLivreById(@PathVariable Long id) {
        Optional<Livre> livre = livreRepository.findById(id);
        return livre.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Créer un nouveau livre (accessible uniquement aux administrateurs)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Livre> createLivre(@RequestBody Livre livre) {
        Optional<Categorie> categorie = categorieRepository.findById(livre.getCategorie().getId());
        if (!categorie.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        livre.setCategorie(categorie.get());
        Livre nouveauLivre = livreRepository.save(livre);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouveauLivre);
    }

    // Mettre à jour un livre (accessible uniquement aux administrateurs)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Livre> updateLivre(@PathVariable Long id, @RequestBody Livre livreDetails) {
        Optional<Livre> livreExistant = livreRepository.findById(id);
        if (!livreExistant.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Livre livre = livreExistant.get();
        livre.setTitre(livreDetails.getTitre());
        livre.setAuteur(livreDetails.getAuteur());
        livre.setDisponible(livreDetails.isDisponible());
        livre.setImage(livreDetails.getImage());

        Optional<Categorie> categorie = categorieRepository.findById(livreDetails.getCategorie().getId());
        if (!categorie.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        livre.setCategorie(categorie.get());
        Livre livreMisAJour = livreRepository.save(livre);
        return ResponseEntity.ok(livreMisAJour);
    }

    // Supprimer un livre (accessible uniquement aux administrateurs)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLivre(@PathVariable Long id) {
        if (!livreRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        livreRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Vérifier si un livre est disponible
    @GetMapping("/{id}/disponible")
    public ResponseEntity<Boolean> verifierDisponibilite(@PathVariable Long id) {
        Livre livre = livreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livre non trouvé"));
        return ResponseEntity.ok(livre.isDisponible());
    }

    // Rendre un livre indisponible
    @PutMapping("/{id}/indisponible")
    public ResponseEntity<String> rendreIndisponible(@PathVariable Long id) {
        Livre livre = livreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livre non trouvé"));
        if (!livre.isDisponible()) {
            return ResponseEntity.status(400).body("Livre déjà indisponible.");
        }
        livre.setDisponible(false);
        livreRepository.save(livre);
        return ResponseEntity.ok("Livre rendu indisponible.");
    }

    // Rendre un livre disponible
    @PutMapping("/{id}/disponible")
    public ResponseEntity<String> rendreDisponible(@PathVariable Long id) {
        Livre livre = livreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livre non trouvé"));
        if (livre.isDisponible()) {
            return ResponseEntity.status(400).body("Livre déjà disponible.");
        }
        livre.setDisponible(true); // Rendre le livre disponible
        livreRepository.save(livre);
        return ResponseEntity.ok("Livre rendu disponible.");
    }

}
