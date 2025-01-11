package com.bibliotheque.universitaire.servicelivres.controller;

import com.bibliotheque.universitaire.servicelivres.model.Categorie;
import com.bibliotheque.universitaire.servicelivres.model.Livre;
import com.bibliotheque.universitaire.servicelivres.repository.CategorieRepository;
import com.bibliotheque.universitaire.servicelivres.repository.LivreRepository;
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

    // 1. Récupérer tous les livres (accessible uniquement aux administrateurs)
    @GetMapping // Seuls les admins peuvent accéder à cette route
    public ResponseEntity<List<Livre>> getAllLivres() {
        return ResponseEntity.ok(livreRepository.findAll());
    }

    // 2. Récupérer un livre par ID (accessible uniquement aux administrateurs)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Seuls les admins peuvent accéder à cette route
    public ResponseEntity<Livre> getLivreById(@PathVariable Long id) {
        Optional<Livre> livre = livreRepository.findById(id);
        return livre.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 3. Créer un nouveau livre (accessible uniquement aux administrateurs)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Seuls les admins peuvent accéder à cette route
    public ResponseEntity<Livre> createLivre(@RequestBody Livre livre) {
        Optional<Categorie> categorie = categorieRepository.findById(livre.getCategorie().getId());
        if (!categorie.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        livre.setCategorie(categorie.get());
        Livre nouveauLivre = livreRepository.save(livre);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouveauLivre);
    }

    // 4. Mettre à jour un livre existant (accessible uniquement aux administrateurs)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Seuls les admins peuvent accéder à cette route
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

    // 5. Supprimer un livre (accessible uniquement aux administrateurs)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Seuls les admins peuvent accéder à cette route
    public ResponseEntity<Void> deleteLivre(@PathVariable Long id) {
        if (!livreRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        livreRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}