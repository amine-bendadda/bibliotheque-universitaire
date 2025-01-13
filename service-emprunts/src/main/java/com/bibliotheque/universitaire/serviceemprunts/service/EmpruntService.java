package com.bibliotheque.universitaire.serviceemprunts.service;

import com.bibliotheque.universitaire.serviceemprunts.model.Emprunt;
import com.bibliotheque.universitaire.serviceemprunts.repository.EmpruntRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmpruntService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EmpruntRepository empruntRepository;

    public Emprunt creerEmprunt(Emprunt emprunt) {
        return empruntRepository.save(emprunt);
    }

    public List<Emprunt> listerEmpruntsEnRetard() {
        return empruntRepository.findByDateRetourBefore(LocalDate.now());
    }

    public Emprunt miseAJourPenalite(Long empruntId, double penalite) {
        // Rechercher l'emprunt par ID
        Emprunt emprunt = empruntRepository.findById(empruntId)
                .orElseThrow(() -> new RuntimeException("Emprunt non trouvé"));

        // Mettre à jour la pénalité
        emprunt.setPenalite(penalite);

        // Enregistrer les modifications dans la base de données
        return empruntRepository.save(emprunt);
    }


    public List<Emprunt> recupererEmpruntsParUtilisateur(String utilisateurId) {
        return empruntRepository.findByUtilisateurId(utilisateurId);
    }

    public List<Emprunt> recupererTousLesEmprunts() {
        return empruntRepository.findAll();
    }

    public Emprunt modifierDateRetour(Long empruntId, LocalDate nouvelleDateRetour) {
        Emprunt emprunt = empruntRepository.findById(empruntId)
                .orElseThrow(() -> new RuntimeException("Emprunt non trouvé"));

        emprunt.setDateRetour(nouvelleDateRetour);

        return empruntRepository.save(emprunt);
    }

    public Emprunt recupererEmpruntParId(Long id) {
        return empruntRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emprunt non trouvé"));
    }

    public void supprimerEmprunt(Long id) {
        empruntRepository.deleteById(id);
    }

}
