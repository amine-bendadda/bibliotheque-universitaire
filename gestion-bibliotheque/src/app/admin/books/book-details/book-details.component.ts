import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common'; // Importer CommonModule
import { BookService, Book } from '../../../services/book.service';
import { ReservationService } from '../../../services/reservation.service';

@Component({
  selector: 'app-book-details',
  standalone: true,
  templateUrl: './book-details.component.html',
  styleUrls: ['./book-details.component.css'],
  imports: [CommonModule], // Ajouter CommonModule ici
})
export class BookDetailsComponent implements OnInit {
  book: Book | undefined;
  reservationReussie: boolean = false; // Indique si la réservation a réussi
  erreurReservation: boolean = false; // Indique s'il y a eu une erreur de réservation

  constructor(
    private route: ActivatedRoute,
    private bookService: BookService,
    private reservationService: ReservationService // Injecter le service de réservation
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    console.log('ID récupéré :', id); // Vérifiez ce log dans la console
    if (id) {
      this.loadBookDetails(+id);
    }
  }

  loadBookDetails(id: number): void {
    this.bookService.getBookById(id).subscribe({
      next: (data) => {
        console.log('Détails du livre récupérés :', data);
        this.book = data; // Stocke les détails du livre
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des détails du livre :', err);
      },
    });
  }

  emprunterLivre(book: any): void {
    if (book.disponible) {
      console.log(`Le livre "${book.titre}" a été emprunté !`);
      // Ajoutez ici la logique pour gérer l'emprunt, comme un appel API
    }
  }

  reserverLivre(): void {
    if (!this.book || !this.book.id) {
      console.error('Impossible de réserver : données du livre manquantes.');
      return;
    }

    this.reservationReussie = false;
    this.erreurReservation = false;

    this.reservationService.reserverLivre(this.book.id.toString()).subscribe({
      next: (response) => {
        console.log('Réservation réussie :', response);
        this.reservationReussie = true; // Affiche une notification de succès
        setTimeout(() => (this.reservationReussie = false), 3000); // Cache la notification après 3 secondes
      },
      error: (err) => {
        console.error('Erreur lors de la réservation :', err);
        this.erreurReservation = true; // Affiche une notification d'erreur
        setTimeout(() => (this.erreurReservation = false), 3000); // Cache la notification après 3 secondes
      },
    });
  }
}
