import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { KeycloakService } from './keycloak.service';

export const httpInterceptor: HttpInterceptorFn = (req, next) => {
  const keycloakService = inject(KeycloakService); // Injecter KeycloakService
  const token = keycloakService.getToken();

  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  return next(req);
};
