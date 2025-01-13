package com.bibliotheque.universitaire.serviceemprunts.controller;

import com.bibliotheque.universitaire.serviceemprunts.model.Emprunt;
import com.bibliotheque.universitaire.serviceemprunts.service.EmpruntService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/emprunts")
public class EmpruntController {

    @Autowired
    private EmpruntService empruntService;

    @Autowired
    private RestTemplate restTemplate;

    // Créer un emprunt (avec l'ID du livre passé dans l'URL)
    @PostMapping("/{livreId}/creer")
    public ResponseEntity<?> creerEmprunt(@PathVariable Long livreId, @RequestBody Emprunt emprunt) {
        // Récupérer l'utilisateur connecté
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String utilisateurId = jwt.getClaimAsString("sub");
        emprunt.setUtilisateurId(utilisateurId); // Associer l'utilisateur connecté à l'emprunt
        emprunt.setDateEmprunt(LocalDate.now()); // Ajouter la date actuelle

        // Vérifier la disponibilité du livre via le microservice "livres"
        String verifierDisponibiliteUrl = "http://localhost:8081/api/livres/" + livreId + "/disponible";
        try {
            Boolean disponible = restTemplate.getForObject(verifierDisponibiliteUrl, Boolean.class);
            if (Boolean.FALSE.equals(disponible)) {
                return ResponseEntity.badRequest().body("Le livre n'est pas disponible pour l'emprunt.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la vérification de la disponibilité du livre : " + e.getMessage());
        }

        // Création de l'emprunt
        emprunt.setLivreId(livreId.toString()); // Associer le livre à l'emprunt
        Emprunt nouvelEmprunt = empruntService.creerEmprunt(emprunt);

        // Rendre le livre indisponible via le microservice "livres"
        String rendreIndisponibleUrl = "http://localhost:8081/api/livres/" + livreId + "/indisponible";
        try {
            restTemplate.put(rendreIndisponibleUrl, null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la mise à jour de la disponibilité du livre : " + e.getMessage());
        }

        return ResponseEntity.ok(nouvelEmprunt);
    }

    @GetMapping("/mes-emprunts")
    public ResponseEntity<?> consulterMesEmprunts() {
        // Récupérer l'ID de l'utilisateur connecté depuis le token JWT
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String utilisateurId = jwt.getClaimAsString("sub"); // Utilisez "sub" ou la clé correspondant à l'ID utilisateur dans votre token JWT

        // Récupérer les emprunts liés à cet utilisateur
        List<Emprunt> mesEmprunts = empruntService.recupererEmpruntsParUtilisateur(utilisateurId);

        // Retourner la liste des emprunts
        return ResponseEntity.ok(mesEmprunts);
    }

    @GetMapping("/admin/tous-les-emprunts")
    @PreAuthorize("hasRole('ADMIN')") // Seuls les administrateurs peuvent accéder à cette route
    public ResponseEntity<List<Emprunt>> consulterTousLesEmprunts() {
        // Récupérer tous les emprunts depuis le service
        List<Emprunt> tousLesEmprunts = empruntService.recupererTousLesEmprunts();

        // Retourner la liste des emprunts
        return ResponseEntity.ok(tousLesEmprunts);
    }

    @PutMapping("/{id}/mise-a-jour-penalite")
    @PreAuthorize("hasRole('ADMIN')") // Seuls les administrateurs peuvent accéder à cette fonctionnalité
    public ResponseEntity<Emprunt> miseAJourPenalite(
            @PathVariable Long id,
            @RequestParam double penalite
    ) {
        // Appeler le service pour mettre à jour la pénalité
        Emprunt empruntMisAJour = empruntService.miseAJourPenalite(id, penalite);

        // Retourner l'emprunt mis à jour
        return ResponseEntity.ok(empruntMisAJour);
    }

    @GetMapping("/en-retard")
    @PreAuthorize("hasRole('ADMIN')") // Seuls les administrateurs peuvent consulter les emprunts en retard
    public ResponseEntity<List<Emprunt>> listerEmpruntsEnRetard() {
        // Appeler le service pour récupérer les emprunts en retard
        List<Emprunt> empruntsEnRetard = empruntService.listerEmpruntsEnRetard();

        // Retourner la liste des emprunts en retard
        return ResponseEntity.ok(empruntsEnRetard);
    }

    @PutMapping("/{id}/modifier-date-retour")
    @PreAuthorize("hasRole('ADMIN')") // Seuls les administrateurs peuvent modifier la date de retour
    public ResponseEntity<Emprunt> modifierDateRetour(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        // Récupérer la nouvelle date depuis le corps de la requête
        String nouvelleDateRetourStr = request.get("dateRetour");
        if (nouvelleDateRetourStr == null || nouvelleDateRetourStr.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        try {
            LocalDate nouvelleDateRetour = LocalDate.parse(nouvelleDateRetourStr);

            // Appeler le service pour mettre à jour la date de retour
            Emprunt empruntMisAJour = empruntService.modifierDateRetour(id, nouvelleDateRetour);

            return ResponseEntity.ok(empruntMisAJour);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @PutMapping("/{id}/retourner-livre")
    public ResponseEntity<?> retournerLivre(@PathVariable Long id) {
        // Récupérer l'emprunt à partir de l'ID
        Emprunt emprunt = empruntService.recupererEmpruntParId(id);
        if (emprunt == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Emprunt non trouvé.");
        }

        // URL pour marquer le livre comme disponible dans le service livres
        String disponibiliteUrl = "http://localhost:8081/api/livres/" + emprunt.getLivreId() + "/disponible";

        try {
            // Mettre à jour la disponibilité du livre via RestTemplate
            restTemplate.put(disponibiliteUrl, null);

            // Supprimer ou mettre à jour l'emprunt après le retour du livre
            empruntService.supprimerEmprunt(emprunt.getId());

            return ResponseEntity.ok("Livre retourné avec succès et marqué comme disponible.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la mise à jour de la disponibilité du livre : " + e.getMessage());
        }
    }


}
