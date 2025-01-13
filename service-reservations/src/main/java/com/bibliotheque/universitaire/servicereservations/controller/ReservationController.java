package com.bibliotheque.universitaire.servicereservations.controller;

import com.bibliotheque.universitaire.servicereservations.model.Reservation;
import com.bibliotheque.universitaire.servicereservations.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/{livreId}/reserver")
    public ResponseEntity<?> reserverLivre(@PathVariable String livreId) {
        // Récupérer l'utilisateur connecté
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String utilisateurId = jwt.getClaimAsString("sub");

        // Vérifier si le livre est indisponible dans le microservice livres
        String verifierDisponibiliteUrl = "http://localhost:8081/api/livres/" + livreId + "/disponible";
        try {
            Boolean disponible = restTemplate.getForObject(verifierDisponibiliteUrl, Boolean.class);
            if (Boolean.TRUE.equals(disponible)) {
                return ResponseEntity.badRequest().body("Le livre est disponible. Pas besoin de le réserver.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur lors de la vérification de la disponibilité du livre : " + e.getMessage());
        }

        // Créer une réservation
        try {
            Reservation reservation = reservationService.creerReservation(utilisateurId, livreId);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/annuler")
    public ResponseEntity<?> annulerReservation(@PathVariable Long id) {
        try {
            // Récupérer l'utilisateur connecté
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String utilisateurId = jwt.getClaimAsString("sub");

            // Vérifier si la réservation appartient à l'utilisateur connecté
            Reservation reservation = reservationService.recupererReservationParId(id)
                    .orElseThrow(() -> new RuntimeException("Réservation non trouvée."));
            if (!reservation.getUtilisateurId().equals(utilisateurId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous ne pouvez annuler que vos propres réservations.");
            }

            // Annuler la réservation
            reservationService.annulerReservation(id);
            return ResponseEntity.ok("Réservation annulée avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'annulation de la réservation : " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/toutes-les-reservations")
    public ResponseEntity<?> afficherToutesLesReservations() {
        try {
            // Récupérer toutes les réservations
            List<Reservation> toutesLesReservations = reservationService.recupererToutesLesReservations();

            return ResponseEntity.ok(toutesLesReservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération de toutes les réservations : " + e.getMessage());
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/livres-reserves")
    public ResponseEntity<?> afficherDetailsLivresEnReservation() {
        try {
            // Appeler le service pour récupérer les informations complètes des livres réservés
            List<Object> detailsLivresReserves = reservationService.recupererTousLesLivresReserves();
            return ResponseEntity.ok(detailsLivresReserves);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des détails des livres réservés : " + e.getMessage());
        }
    }

    @GetMapping("/mes-reservations")
    public ResponseEntity<?> afficherDetailsDeMesReservations() {
        try {
            // Appeler le service pour récupérer les détails des réservations
            List<Object> detailsReservations = reservationService.recupererDetailsDeMesReservations();
            return ResponseEntity.ok(detailsReservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des détails de vos réservations : " + e.getMessage());
        }
    }


}
