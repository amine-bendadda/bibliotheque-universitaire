package com.bibliotheque.universitaire.servicereservations.service;

import com.bibliotheque.universitaire.servicereservations.model.Reservation;
import com.bibliotheque.universitaire.servicereservations.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public Reservation creerReservation(String utilisateurId, String livreId) {
        Reservation reservation = new Reservation();
        reservation.setUtilisateurId(utilisateurId);
        reservation.setLivreId(livreId);
        reservation.setDateReservation(LocalDate.now());
        reservation.setDisponible(false); // Par défaut, le livre n'est pas encore disponible
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsParUtilisateur(String utilisateurId) {
        return reservationRepository.findByUtilisateurId(utilisateurId);
    }

    public List<Reservation> getToutesReservations() {
        return reservationRepository.findAll();
    }
}
