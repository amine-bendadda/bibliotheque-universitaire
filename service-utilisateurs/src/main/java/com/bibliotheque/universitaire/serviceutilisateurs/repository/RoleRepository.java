package com.bibliotheque.universitaire.serviceutilisateurs.repository;

import com.bibliotheque.universitaire.serviceutilisateurs.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNom(String nom);
}
