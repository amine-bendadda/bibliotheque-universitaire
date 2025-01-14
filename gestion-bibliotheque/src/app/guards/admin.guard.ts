import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { KeycloakService } from '../services/keycloak.service';

@Injectable({
  providedIn: 'root',
})
export class AdminGuard implements CanActivate {
  constructor(private keycloakService: KeycloakService, private router: Router) {}

  canActivate(): boolean {
    const roles = this.keycloakService.getRoles();
    if (roles.includes('ROLE_ADMIN')) {
      return true;
    } else {
      this.router.navigate(['/student']); // Redirige vers la page étudiant si non autorisé
      return false;
    }
  }
}
