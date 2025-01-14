import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';

@Injectable({
  providedIn: 'root',
})
export class KeycloakService {
  private keycloakInstance: Keycloak.KeycloakInstance;

  constructor() {
    this.keycloakInstance = new Keycloak({
      url: 'http://localhost:8080',
      realm: 'bibliotheque',
      clientId: 'angular-client',
    });
  }

  init(): Promise<boolean> {
    return this.keycloakInstance
      .init({
        onLoad: 'login-required', // Forcer la connexion
        checkLoginIframe: false,
        pkceMethod: 'S256', // Activer PKCE
      })
      .then(() => {
        console.log('Keycloak initialisé avec succès.');
        this.redirectBasedOnRole(); // Rediriger en fonction du rôle
        return true;
      })
      .catch((err) => {
        console.error('Erreur lors de l\'initialisation de Keycloak :', err);
        return false;
      });
  }

  getRoles(): string[] {
    return this.keycloakInstance.tokenParsed?.realm_access?.roles || [];
  }
  

  getToken(): string | undefined {
    return this.keycloakInstance.token;
  }

  logout(): void {
    console.log('Déconnexion demandée...');
    const logoutUrl = 'http://localhost:8080/realms/bibliotheque/protocol/openid-connect/logout?redirect_uri=http://localhost:4200';
    window.location.href = logoutUrl;
  }
  
  

  private redirectBasedOnRole(): void {
    const roles = this.getRoles();
    const currentPath = window.location.pathname; // Chemin actuel

    if (roles.includes('ROLE_ADMIN') && currentPath !== '/admin') {
      window.location.href = '/admin';
    } else if (roles.includes('ROLE_USER') && currentPath !== '/student') {
      window.location.href = '/student';
    }
  }
}
