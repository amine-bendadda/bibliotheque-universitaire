package com.bibliotheque.universitaire.servicereservations.service;

import com.bibliotheque.universitaire.servicereservations.model.Reservation;
import com.bibliotheque.universitaire.servicereservations.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Reservation creerReservation(String utilisateurId, String livreId) {
        // Vérifier si le livre est déjà réservé
        Optional<Reservation> reservationExistante = reservationRepository.findByLivreId(livreId);
        if (reservationExistante.isPresent()) {
            throw new RuntimeException("Ce livre est déjà réservé.");
        }

        // Créer une nouvelle réservation
        Reservation reservation = new Reservation();
        reservation.setUtilisateurId(utilisateurId);
        reservation.setLivreId(livreId);
        reservation.setDateReservation(LocalDate.now());
        reservation.setDisponible(false); // Marquer comme non disponible

        return reservationRepository.save(reservation);
    }

    public Optional<Reservation> recupererReservationParId(Long id) {
        return reservationRepository.findById(id);
    }

    public void annulerReservation(Long id) {
        reservationRepository.deleteById(id);
    }


    public List<Reservation> recupererToutesLesReservations() {
        return reservationRepository.findAll();
    }


    public List<Object> recupererTousLesLivresReserves() {
        // Récupérer les IDs des livres réservés
        List<String> livresReservesIds = reservationRepository.findByDisponible(false)
                .stream()
                .map(Reservation::getLivreId)
                .distinct()
                .collect(Collectors.toList());

        // URL de l'API du microservice livres
        String livreServiceUrl = "http://localhost:8081/api/livres/public/";

        // Récupérer les détails pour chaque livre réservé
        List<Object> detailsLivres = new ArrayList<>();
        for (String livreId : livresReservesIds) {
            try {
                // Appeler le microservice livres pour récupérer les détails
                Object detailsLivre = restTemplate.getForObject(livreServiceUrl + livreId, Object.class);
                detailsLivres.add(detailsLivre);
            } catch (Exception e) {
                // En cas d'erreur, ajouter un message pour indiquer que le livre est introuvable
                detailsLivres.add(Map.of("livreId", livreId, "message", "Erreur lors de la récupération des détails"));
            }
        }

        return detailsLivres;
    }


    public List<Object> recupererDetailsDeMesReservations() {
        // Récupérer l'utilisateur connecté à partir du JWT
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String utilisateurId = jwt.getClaimAsString("sub");

        // Récupérer les réservations de l'utilisateur
        List<Reservation> reservations = reservationRepository.findByUtilisateurId(utilisateurId);

        // URL de l'API du microservice livres
        String livreServiceUrl = "http://localhost:8081/api/livres/public/";

        // Récupérer les détails pour chaque réservation
        List<Object> detailsReservations = new ArrayList<>();
        for (Reservation reservation : reservations) {
            try {
                // Appeler le microservice livres pour récupérer les détails du livre
                Object detailsLivre = restTemplate.getForObject(livreServiceUrl + reservation.getLivreId(), Object.class);

                // Ajouter les détails du livre et de la réservation dans la réponse
                Map<String, Object> details = new HashMap<>();
                details.put("reservation", reservation);
                details.put("livre", detailsLivre);

                detailsReservations.add(details);
            } catch (Exception e) {
                // En cas d'erreur, ajouter un message pour indiquer que les détails du livre sont introuvables
                detailsReservations.add(Map.of(
                        "reservation", reservation,
                        "livre", Map.of("message", "Erreur lors de la récupération des détails du livre")
                ));
            }
        }

        return detailsReservations;
    }
}
