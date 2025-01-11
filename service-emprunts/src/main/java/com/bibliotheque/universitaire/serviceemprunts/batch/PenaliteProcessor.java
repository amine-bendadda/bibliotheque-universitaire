package com.bibliotheque.universitaire.serviceemprunts.batch;

import com.bibliotheque.universitaire.serviceemprunts.model.Emprunt;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class PenaliteProcessor implements ItemProcessor<Emprunt, Emprunt> {

    @Override
    public Emprunt process(Emprunt emprunt) {
        if (emprunt.getDateRetour().isBefore(LocalDate.now())) {
            long joursEnRetard = ChronoUnit.DAYS.between(emprunt.getDateRetour(), LocalDate.now());
            emprunt.setPenalite(joursEnRetard * 1.5); // Exemple : 1.5€ par jour de retard
        } else {
            emprunt.setPenalite(0);
        }
        return emprunt;
    }
}
