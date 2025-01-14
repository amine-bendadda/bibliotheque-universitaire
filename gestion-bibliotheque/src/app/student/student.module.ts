import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { StudentRoutingModule } from './student-routing.module';
import { StudentDashboardComponent } from './dashboard/dashboard.component';


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    StudentRoutingModule,
    StudentDashboardComponent
  ]
})
export class StudentModule { }
