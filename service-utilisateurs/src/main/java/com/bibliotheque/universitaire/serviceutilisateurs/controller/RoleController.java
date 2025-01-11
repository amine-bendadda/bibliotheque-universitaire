package com.bibliotheque.universitaire.serviceutilisateurs.controller;

import com.bibliotheque.universitaire.serviceutilisateurs.model.Role;
import com.bibliotheque.universitaire.serviceutilisateurs.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs/roles")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Seuls les admins peuvent lire les rôles
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Seuls les admins peuvent créer des rôles
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        Role nouveauRole = roleRepository.save(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouveauRole);
    }
}
