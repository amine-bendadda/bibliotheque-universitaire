import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BooksRoutingModule } from './books-routing.module';
import { BooksComponent } from './books.component';
import { FormsModule } from '@angular/forms';


@NgModule({
  declarations: [
    BooksComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    BooksRoutingModule
  ]
})
export class BooksModule { }
