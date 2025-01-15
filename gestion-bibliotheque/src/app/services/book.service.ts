import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BookService {
  private apiUrl = 'http://localhost:8081/api/livres/public'; // URL du microservice livres

  constructor(private http: HttpClient) {}

  // Récupérer la liste des livres
  getBooks(): Observable<Book[]> {
    return this.http.get<Book[]>(this.apiUrl);
  }

  // Récupérer un livre par ID
  getBookById(id: number): Observable<Book> {
    return this.http.get<Book>(`${this.apiUrl}/${id}`);
  }
}

// Modèle pour un livre
export interface Book {
  id: number;
  titre: string;
  auteur: string;
  disponible: boolean;
  image: string;
  categorie: {
    id: number;
    nom: string;
  };
}
