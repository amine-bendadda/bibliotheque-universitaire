import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; // Import nécessaire
import { ReservationService } from '../../services/reservation.service';

@Component({
  selector: 'app-reservation',
  standalone: true, // Assurez-vous que le composant est standalone
  templateUrl: './reservation.component.html',
  styleUrls: ['./reservation.component.css'],
  imports: [CommonModule], // Import de CommonModule requis pour *ngIf, *ngFor
})
export class ReservationComponent implements OnInit {
  reservations: any[] = [];
  errorMessage: string | null = null;

  constructor(private reservationService: ReservationService) {}

  ngOnInit(): void {
    this.loadUserReservations();
  }

  loadUserReservations(): void {
    this.reservationService.getMesReservations().subscribe({
      next: (data) => {
        console.log('Réservations récupérées :', data);
        this.reservations = data;
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des réservations :', err);
        this.errorMessage = 'Impossible de récupérer vos réservations.';
      },
    });
  }

  supprimerReservation(reservation: any): void {
    const confirmation = confirm(
      `Êtes-vous sûr de vouloir supprimer la réservation du livre "${reservation.livre.titre}" ?`
    );
    if (confirmation) {
      this.reservationService.annulerReservation(reservation.reservation.id).subscribe({
        next: (response) => {
          if (response.status === 204 || response.status === 200) {
            console.log(`La réservation du livre "${reservation.livre.titre}" a été supprimée.`);
            alert(`La réservation du livre "${reservation.livre.titre}" a été annulée avec succès.`);
            this.loadReservations(); // Recharger les réservations
          }
        },
        error: (err) => {
          console.error('Erreur lors de l\'annulation de la réservation :', err);
          alert('Une erreur est survenue lors de l\'annulation de la réservation.');
        },
      });
    }
  }
  
  loadReservations(): void {
    this.reservationService.getMesReservations().subscribe({
      next: (data) => {
        this.reservations = data;
        console.log('Réservations mises à jour :', this.reservations);
      },
      error: (err) => {
        console.error('Erreur lors du chargement des réservations :', err);
      },
    });
  }
  
  
}
