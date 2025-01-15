import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { StudentDashboardComponent } from './dashboard/dashboard.component';
import { BookListComponent } from './books/book-list/book-list.component';
import { ReservationComponent } from './reservation/reservation.component';
import { BookDetailsComponent } from './books/book-details/book-details.component';

const routes: Routes = [
  { path: '', component: StudentDashboardComponent },
  { path: 'books', component: BookListComponent },
  { path: 'books/:id', component: BookDetailsComponent },
  { path: 'reservations', component: ReservationComponent },
];



@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class StudentRoutingModule {}


