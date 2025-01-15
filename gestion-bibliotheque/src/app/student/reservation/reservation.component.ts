import { Component } from '@angular/core';
import { ReservationService } from '../../services/reservation.service';

@Component({
  selector: 'app-reservation',
  templateUrl: './reservation.component.html',
})
export class ReservationComponent {
  constructor(private reservationService: ReservationService) {}

  reserverLivre(livreId: string): void {
    this.reservationService.reserverLivre(livreId).subscribe({
      next: (response) => {
        console.log('Réservation réussie :', response);
        alert('Réservation réussie !');
      },
      error: (error) => {
        console.error('Erreur lors de la réservation :', error);
        alert('Erreur lors de la réservation.');
      },
    });
  }
}
