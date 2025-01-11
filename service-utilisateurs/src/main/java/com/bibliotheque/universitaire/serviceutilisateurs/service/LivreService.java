package com.bibliotheque.universitaire.serviceutilisateurs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LivreService {

    @Autowired
    private RestTemplate restTemplate;

    public String getLivres(String jwtToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken); // Ajoute le token JWT

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://SERVICE-LIVRES/api/livres", // URL du microservice livres
                HttpMethod.GET,
                entity,
                String.class
        );

        return response.getBody();
    }
}