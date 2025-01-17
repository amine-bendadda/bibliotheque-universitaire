package com.bibliotheque.universitaire.servicelivres.repository;

import com.bibliotheque.universitaire.servicelivres.model.Livre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivreRepository extends JpaRepository<Livre, Long> {
}
