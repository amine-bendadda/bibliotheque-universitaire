import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'admin',
    loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule),
  },
  {
    path: 'student',
    loadChildren: () => import('./student/student.module').then(m => m.StudentModule),
  },
  { path: '', redirectTo: '/student', pathMatch: 'full' }, // Redirection par défaut
  { path: '**', redirectTo: '/student', pathMatch: 'full' }, // Gestion des routes inconnues
];
