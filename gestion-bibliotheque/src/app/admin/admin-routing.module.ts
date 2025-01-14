import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminDashboardComponent } from './dashboard/dashboard.component';

const routes: Routes = [
  { path: '', component: AdminDashboardComponent },
  // { path: 'users', component: UserManagementComponent },
  // { path: 'books', component: BookManagementComponent },
  // { path: 'reservations', component: ReservationManagementComponent },
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AdminRoutingModule {}