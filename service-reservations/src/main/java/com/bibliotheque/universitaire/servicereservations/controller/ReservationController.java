package com.bibliotheque.universitaire.servicereservations.controller;

import com.bibliotheque.universitaire.servicereservations.model.Reservation;
import com.bibliotheque.universitaire.servicereservations.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Reservation> creerReservation(@RequestParam String utilisateurId, @RequestParam String livreId) {
        Reservation reservation = reservationService.creerReservation(utilisateurId, livreId);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<Reservation>> getReservationsParUtilisateur(@PathVariable String utilisateurId) {
        return ResponseEntity.ok(reservationService.getReservationsParUtilisateur(utilisateurId));
    }

    @GetMapping("/admin/toutes")
    public ResponseEntity<List<Reservation>> getToutesReservations() {
        return ResponseEntity.ok(reservationService.getToutesReservations());
    }
}
