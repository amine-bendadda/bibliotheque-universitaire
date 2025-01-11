package com.bibliotheque.universitaire.servicelivres.controller;

import com.bibliotheque.universitaire.servicelivres.model.Categorie;
import com.bibliotheque.universitaire.servicelivres.repository.CategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/livres/categories")
public class CategorieController {

    @Autowired
    private CategorieRepository categorieRepository;

    // 1. Récupérer toutes les catégories
    @GetMapping
    public ResponseEntity<List<Categorie>> getAllCategories() {
        List<Categorie> categories = categorieRepository.findAll();
        return ResponseEntity.ok(categories);
    }

    // 2. Récupérer une catégorie par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Categorie> getCategorieById(@PathVariable Long id) {
        Optional<Categorie> categorie = categorieRepository.findById(id);
        return categorie.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 3. Créer une nouvelle catégorie
    @PostMapping
    public ResponseEntity<Categorie> createCategorie(@RequestBody Categorie categorie) {
        Categorie nouvelleCategorie = categorieRepository.save(categorie);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleCategorie);
    }

    // 4. Mettre à jour une catégorie existante
    @PutMapping("/{id}")
    public ResponseEntity<Categorie> updateCategorie(@PathVariable Long id, @RequestBody Categorie categorieDetails) {
        Optional<Categorie> categorieExistante = categorieRepository.findById(id);
        if (categorieExistante.isPresent()) {
            Categorie categorie = categorieExistante.get();
            categorie.setNom(categorieDetails.getNom()); // Met à jour le nom
            return ResponseEntity.ok(categorieRepository.save(categorie));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 5. Supprimer une catégorie
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategorie(@PathVariable Long id) {
        if (categorieRepository.existsById(id)) {
            categorieRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
