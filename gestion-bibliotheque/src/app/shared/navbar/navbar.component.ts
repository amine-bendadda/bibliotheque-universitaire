import { Component } from '@angular/core';
import { KeycloakService } from '../../services/keycloak.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
  standalone: true,
})
export class NavbarComponent {
  constructor(private keycloakService: KeycloakService, private router: Router) {}

  isAdmin(): boolean {
    return this.keycloakService.getRoles().includes('ROLE_ADMIN');
  }

  isUser(): boolean {
    return this.keycloakService.getRoles().includes('ROLE_USER');
  }

  logout(): void {
    this.keycloakService.logout();
  }
}
