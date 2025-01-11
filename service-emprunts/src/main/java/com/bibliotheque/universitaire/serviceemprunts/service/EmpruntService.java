package com.bibliotheque.universitaire.serviceemprunts.service;

import com.bibliotheque.universitaire.serviceemprunts.model.Emprunt;
import com.bibliotheque.universitaire.serviceemprunts.repository.EmpruntRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmpruntService {

    @Autowired
    private EmpruntRepository empruntRepository;

    public Emprunt creerEmprunt(Emprunt emprunt) {
        return empruntRepository.save(emprunt);
    }

    public List<Emprunt> listerEmpruntsEnRetard() {
        return empruntRepository.findByDateRetourBefore(LocalDate.now());
    }

    public Emprunt miseAJourPenalite(Long empruntId, double penalite) {
        Emprunt emprunt = empruntRepository.findById(empruntId)
                .orElseThrow(() -> new RuntimeException("Emprunt non trouvé"));
        emprunt.setPenalite(penalite);
        return empruntRepository.save(emprunt);
    }
}
