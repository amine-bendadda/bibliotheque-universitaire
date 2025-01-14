import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { StudentDashboardComponent } from './dashboard/dashboard.component';

const routes: Routes = [
  { path: '', component: StudentDashboardComponent },
  // { path: 'books', component: BookSearchComponent },
  // { path: 'reservations', component: MyReservationsComponent },
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class StudentRoutingModule {}
