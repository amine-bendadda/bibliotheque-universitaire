import { Component } from '@angular/core';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'], // Ajoutez un fichier CSS si nécessaire
})
export class AdminDashboardComponent {
  title = 'Tableau de bord administrateur';
  description = 'Gérez les utilisateurs, les livres, et les réservations depuis ce tableau de bord.';
}