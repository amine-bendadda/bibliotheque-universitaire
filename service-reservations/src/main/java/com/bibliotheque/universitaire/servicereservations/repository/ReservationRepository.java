package com.bibliotheque.universitaire.servicereservations.repository;

import com.bibliotheque.universitaire.servicereservations.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUtilisateurId(String utilisateurId);
    Optional<Reservation> findByLivreId(String livreId);
    List<Reservation> findByDisponible(boolean disponible);

}
