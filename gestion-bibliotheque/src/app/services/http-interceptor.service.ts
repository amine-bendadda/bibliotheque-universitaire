// import { HttpInterceptorFn } from '@angular/common/http';
// import { inject } from '@angular/core';
// import { KeycloakService } from './keycloak.service';

// export const httpInterceptor: HttpInterceptorFn = (req, next) => {
//   const keycloakService = inject(KeycloakService); // Injecter KeycloakService
//   const token = keycloakService.getToken();

//   if (token) {
//     req = req.clone({
//       setHeaders: {
//         Authorization: `Bearer ${token}`,
//       },
//     });
//   }

//   return next(req);
// };

// import { HttpEvent, HttpHandlerFn, HttpRequest } from '@angular/common/http';
// import { inject } from '@angular/core';
// import { Observable } from 'rxjs'; // Import de Observable
// import { KeycloakService } from './keycloak.service';
// import { Injectable } from '@angular/core';
// import { HttpInterceptor } from '@angular/common/http';
// import { HttpHandler } from '@angular/common/http';

// @Injectable()
// export class HttpInterceptorService implements HttpInterceptor {
//   constructor(private keycloakService: KeycloakService) {}

//   intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
//     const token = this.keycloakService.getToken();

//     if (token) {
//       const clonedRequest = req.clone({
//         setHeaders: {
//           Authorization: `Bearer ${token}`,
//         },
//       });
//       return next.handle(clonedRequest);
//     } else {
//       console.warn('Aucun JWT trouvé, la requête peut échouer.');
//       return next.handle(req);
//     }
//   }
// }

import { HttpRequest, HttpHandlerFn, HttpEvent } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable } from 'rxjs';
import { KeycloakService } from './keycloak.service';

export function httpInterceptorFn(req: HttpRequest<any>, next: HttpHandlerFn): Observable<HttpEvent<any>> {
  // Injection du service Keycloak
  const keycloakService = inject(KeycloakService);
  const token = keycloakService.getToken();

  if (token) {
    // Clonage de la requête avec l'en-tête Authorization
    const clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
    return next(clonedRequest);
  } else {
    console.warn('Aucun JWT trouvé, la requête peut échouer.');
    return next(req);
  }
}

