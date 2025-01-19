import { Component } from '@angular/core';
import { BookService, Book, Categorie } from '../../../services/book.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-add-book',
  standalone: true,
  templateUrl: './add-book.component.html',
  styleUrls: ['./add-book.component.css'],
  imports: [FormsModule],
})
export class AddBookComponent {
  newBook: Partial<Book> = {
    titre: '',
    auteur: '',
    image: '',
    disponible: true,
    categorie: { id: 0, nom: '' }, // Initialisation avec un objet par défaut
  };
  
  selectedCategoryId: number = 0; // Propriété pour lier l'ID de catégorie
  categories: Categorie[] = [];

  constructor(private bookService: BookService, private router: Router) {}

  ngOnInit(): void {
    // Charger les catégories au démarrage
    this.bookService.getCategories().subscribe({
      next: (data) => {
        this.categories = data;
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des catégories :', err);
      },
    });
  }

  onSubmit(): void {
    if (!this.newBook.titre || !this.newBook.auteur || this.selectedCategoryId === 0) {
      alert('Veuillez remplir tous les champs requis.');
      return;
    }

    // Associez l'ID de la catégorie sélectionnée
    this.newBook.categorie = { id: this.selectedCategoryId, nom: '' };

    this.bookService.addBook(this.newBook).subscribe({
      next: (response) => {
        console.log('Livre ajouté avec succès :', response);
        alert('Livre ajouté avec succès.');
        this.router.navigate(['/admin/books']); // Redirection vers la liste des livres
      },
      error: (err) => {
        console.error("Erreur lors de l'ajout du livre :", err);
        alert("Erreur lors de l'ajout du livre.");
      },
    });
  }

  resetForm(): void {
    this.newBook = {
      titre: '',
      auteur: '',
      image: '',
      disponible: true,
      categorie: { id: 0, nom: '' },
    };
    this.selectedCategoryId = 0; // Réinitialisez également l'ID de la catégorie
  }
  
}

