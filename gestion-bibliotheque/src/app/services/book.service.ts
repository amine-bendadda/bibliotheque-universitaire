// import { Injectable } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import { Observable } from 'rxjs';

// @Injectable({
//   providedIn: 'root',
// })
// export class BookService {
//   private apiUrl = 'http://localhost:8081/api/livres/public'; // URL du microservice livres

//   constructor(private http: HttpClient) {}

//   // Récupérer la liste des livres
//   getBooks(): Observable<Book[]> {
//     return this.http.get<Book[]>(this.apiUrl);
//   }

//   // Récupérer un livre par ID
//   getBookById(id: number): Observable<Book> {
//     return this.http.get<Book>(`${this.apiUrl}/${id}`);
//   }
// }

// // Modèle pour un livre
// export interface Book {
//   id: number;
//   titre: string;
//   auteur: string;
//   disponible: boolean;
//   image: string;
//   categorie: {
//     id: number;
//     nom: string;
//   };
// }

// BookService - Ajout de méthode pour créer un livre
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BookService {
  private apiUrl = 'http://localhost:8081/api/livres'; // URL du microservice livres

  constructor(private http: HttpClient) {}

  // Récupérer la liste des livres
  getBooks(): Observable<Book[]> {
    return this.http.get<Book[]>(`${this.apiUrl}/public`);
  }

  // Récupérer un livre par ID
  getBookById(id: number): Observable<Book> {
    return this.http.get<Book>(`${this.apiUrl}/public/${id}`);
  }

  // Ajouter un nouveau livre
  addBook(livre: Partial<Book>): Observable<Book> {
    return this.http.post<Book>(this.apiUrl, livre); // Appel API POST pour ajouter un livre
  }

  // Récupérer la liste des catégories
  getCategories(): Observable<Categorie[]> {
    return this.http.get<Categorie[]>(`${this.apiUrl}/categories`);
  }
}

// Modèle pour une categorie
export interface Categorie {
  id: number;
  nom: string;
}

// Modèle pour un livre
export interface Book {
  id: number;
  titre: string;
  auteur: string;
  disponible: boolean;
  image: string;
  categorie: Categorie; // Utilisez `Categorie` ici
}
