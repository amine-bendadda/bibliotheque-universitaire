import { Component, OnInit } from '@angular/core';
import { ReservationService } from '../../services/reservation.service';

@Component({
  selector: 'app-admin-reservations',
  standalone: true,
  templateUrl: './reservations.component.html',
  styleUrls: ['./reservations.component.css'],
  imports: [],
})
export class ReservationsComponent implements OnInit {
  reservations: any[] = []; // Liste pour stocker les réservations
  errorMessage: string | null = null;

  constructor(private reservationService: ReservationService) {}

  ngOnInit(): void {
    this.fetchReservations();
  }

  fetchReservations(): void {
    this.reservationService.getToutesLesReservations().subscribe({
      next: (data) => {
        this.reservations = data;
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des réservations :', err);
        this.errorMessage = 'Impossible de charger les réservations.';
      },
    });
  }
}
