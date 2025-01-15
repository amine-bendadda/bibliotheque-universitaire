import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ReservationService {
  private readonly baseUrl = 'http://localhost:8084/api/reservations'; // Base URL du microservice

  constructor(private http: HttpClient) {}

  // Construire une URL pour les appels dynamiques
  construireUrl(livreId: string): string {
    return `${this.baseUrl}/${livreId}/reserver`;
  }

  // Réserver un livre
  reserverLivre(livreId: string): Observable<any> {
    const url = this.construireUrl(livreId);
    return this.http.post(url, null); // Appel au microservice de réservation
  }

  // Annuler une réservation
  annulerReservation(reservationId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${reservationId}/annuler`);
  }

  // Récupérer toutes les réservations pour l'administrateur
  getToutesLesReservations(): Observable<any> {
    return this.http.get(`${this.baseUrl}/admin/toutes-les-reservations`);
  }

  // Récupérer les livres réservés avec leurs détails (administrateur)
  getLivresReservesDetails(): Observable<any> {
    return this.http.get(`${this.baseUrl}/admin/livres-reserves`);
  }

  // Récupérer les détails des réservations pour l'utilisateur connecté
  getMesReservations(): Observable<any> {
    return this.http.get(`${this.baseUrl}/mes-reservations`);
  }
}
