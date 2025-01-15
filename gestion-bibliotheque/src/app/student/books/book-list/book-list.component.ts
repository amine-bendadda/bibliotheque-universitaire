import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router'; // Importer RouterModule
import { FormsModule } from '@angular/forms';
import { BookService, Book } from '../../../services/book.service';

@Component({
  selector: 'app-book-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule], // Ajouter RouterModule
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.css'],
})
export class BookListComponent implements OnInit {
  books: Book[] = [];
  filteredBooks: Book[] = [];
  categories: string[] = [];
  searchText: string = '';
  selectedCategory: string = '';
  selectedAvailability: string = '';

  constructor(private bookService: BookService, private router: Router) {} 

  ngOnInit(): void {
    this.loadBooks();
  }

  loadBooks(): void {
    this.bookService.getBooks().subscribe({
      next: (data) => {
        this.books = data;
        this.filteredBooks = data; // Initialiser les livres filtrés
        this.extractCategories(); // Extraire les catégories uniques
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des livres :', err);
      },
    });
  }

  extractCategories(): void {
    this.categories = [...new Set(this.books.map((book) => book.categorie.nom))];
  }

  filterBooks(): void {
    this.filteredBooks = this.books.filter((book) => {
      const matchesSearch =
        !this.searchText ||
        book.titre.toLowerCase().includes(this.searchText.toLowerCase()) ||
        book.auteur.toLowerCase().includes(this.searchText.toLowerCase());
      const matchesCategory =
        !this.selectedCategory || book.categorie.nom === this.selectedCategory;
      const matchesAvailability =
        !this.selectedAvailability ||
        String(book.disponible) === this.selectedAvailability;

      return matchesSearch && matchesCategory && matchesAvailability;
    });
  }

  navigateToDetails(bookId: number): void {
    if (bookId) {
      const url = `/student/books/${bookId}`; // Assurez-vous que le chemin correspond
      console.log(`Redirection vers : ${url}`);
      this.router.navigate([url]).then(success => {
        if (success) {
          console.log(`Navigation réussie vers ${url}`);
        } else {
          console.error(`Échec de la navigation vers ${url}`);
        }
      });
    }
  }
  
  
  
}
