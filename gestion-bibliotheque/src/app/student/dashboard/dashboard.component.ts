import { Component } from '@angular/core';

@Component({
  selector: 'app-student-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'], // Ajoutez un fichier CSS si nécessaire
})
export class StudentDashboardComponent {
  title = 'Tableau de bord étudiant';
  description = 'Recherchez et réservez des livres depuis ce tableau de bord.';
}
