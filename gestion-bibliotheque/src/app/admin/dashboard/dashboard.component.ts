import { Router } from '@angular/router';
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule], // Importez ici les modules nécessaires
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'], // Ajoutez un fichier CSS si nécessaire
})
export class AdminDashboardComponent {
  title = 'Tableau de bord administrateur';
  description = 'Gérez les utilisateurs, les livres, et les réservations depuis ce tableau de bord.';

  constructor(private router: Router) {}

  redirectToKeycloakAccount(): void {
    const accountUrl = 'http://localhost:8080/realms/bibliotheque/account/';
    window.location.href = accountUrl; // Redirige l'utilisateur
  }

  redirectTo(path: string): void {
    if (path) {
      this.router.navigate([path]).then((success) => {
        if (success) {
          console.log(`Navigation réussie vers ${path}`);
        } else {
          console.error(`Échec de la navigation vers ${path}`);
        }
      });
    } else {
      console.error('Chemin de redirection invalide.');
    }
  }
}

