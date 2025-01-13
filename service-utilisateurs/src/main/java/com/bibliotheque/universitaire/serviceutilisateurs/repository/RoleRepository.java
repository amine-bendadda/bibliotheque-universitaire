package com.bibliotheque.universitaire.serviceutilisateurs.repository;

import com.bibliotheque.universitaire.serviceutilisateurs.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByNom(String nom);
}
