// import { NgModule } from '@angular/core';
// import { RouterModule, Routes } from '@angular/router';
// import { AdminDashboardComponent } from './dashboard/dashboard.component';
// import { BookListComponent } from './books/book-list/book-list.component';
// import { BookDetailsComponent } from './books/book-details/book-details.component';
// import { AddBookComponent } from './books/add-book/add-book.component';

// const routes: Routes = [
//   { path: '', component: AdminDashboardComponent },
//   { path: 'books/add-book', component: AddBookComponent }, // Placez cette route avant :id
//   { path: 'books/:id', component: BookDetailsComponent }, // Route dynamique
//   { path: 'books', component: BookListComponent },
// ];


// @NgModule({
//   imports: [RouterModule.forChild(routes)],
//   exports: [RouterModule],
// })
// export class AdminRoutingModule {}

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminDashboardComponent } from './dashboard/dashboard.component';
import { BookListComponent } from './books/book-list/book-list.component';
import { BookDetailsComponent } from './books/book-details/book-details.component';
import { ReservationComponent } from '../student/reservation/reservation.component';

const routes: Routes = [
  { path: '', component: AdminDashboardComponent },
  { path: 'books', component: BookListComponent },
  {
    path: 'books/add-book',
    loadComponent: () =>
      import('./books/add-book/add-book.component').then(
        (m) => m.AddBookComponent
      ),
  },
  { path: 'books/:id', component: BookDetailsComponent },
  { path: 'all-reservations', component: ReservationComponent },
  
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AdminRoutingModule {}
