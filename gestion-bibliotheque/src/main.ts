// import { bootstrapApplication } from '@angular/platform-browser';
// import { AppComponent } from './app/app.component';
// import { appConfig } from './app/app.config';
// import { KeycloakService } from './app/services/keycloak.service';

// const keycloakService = new KeycloakService();

// keycloakService.init().then((authenticated) => {
//   if (authenticated) {
//     bootstrapApplication(AppComponent, appConfig)
//       .catch((err) => console.error(err));
//   } else {
//     console.error('Utilisateur non authentifié. Redirection vers Keycloak échouée.');
//   }
// }).catch((err) => {
//   console.error('Erreur lors de l\'initialisation de Keycloak :', err);
// });


import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';
import { KeycloakService } from './app/services/keycloak.service';

const keycloakService = new KeycloakService();

keycloakService.init().then(() => {
  console.log('Keycloak initialisé, démarrage de l\'application...');
  bootstrapApplication(AppComponent, appConfig).catch((err) =>
    console.error('Erreur lors du démarrage de l\'application Angular:', err)
  );
}).catch((err) => {
  console.error('Erreur lors de l\'initialisation de Keycloak:', err);
});
