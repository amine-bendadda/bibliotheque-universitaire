package com.bibliotheque.universitaire.serviceemprunts.repository;

import com.bibliotheque.universitaire.serviceemprunts.model.Emprunt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface EmpruntRepository extends JpaRepository<Emprunt, Long> {
    List<Emprunt> findByDateRetourBefore(LocalDate date);
    List<Emprunt> findByUtilisateurId(String utilisateurId);
}
