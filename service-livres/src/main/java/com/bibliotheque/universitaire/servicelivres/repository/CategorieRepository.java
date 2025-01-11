package com.bibliotheque.universitaire.servicelivres.repository;

import com.bibliotheque.universitaire.servicelivres.model.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategorieRepository extends JpaRepository<Categorie, Long> {
}
