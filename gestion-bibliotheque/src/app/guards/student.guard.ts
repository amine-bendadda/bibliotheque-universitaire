
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { KeycloakService } from '../services/keycloak.service';

@Injectable({
  providedIn: 'root',
})
export class StudentGuard implements CanActivate {
  constructor(private keycloakService: KeycloakService, private router: Router) {}

  canActivate(): boolean {
    const roles = this.keycloakService.getRoles();
    if (roles.includes('ROLE_USER')) {
      return true; // Accès autorisé
    }
    this.router.navigate(['/']); // Redirection si l'utilisateur n'est pas autorisé
    return false;
  }
}
