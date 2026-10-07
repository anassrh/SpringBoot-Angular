# TP météo — Spring Boot et Angular

Le backend a été démarré et testé par l'utilisateur : Paris renvoie les données météo, une ville vide renvoie 400 et une ville inexistante renvoie 404. Le PDF fourni reste inaccessible dans cette session. Le code suit la demande détaillée du texte collé.

Le frontend a été créé après ces validations. Il appelle uniquement le backend, sur le port 8081 choisi pour éviter le conflit sur 8080.

## Fichiers créés

~~~text
meteo-tp/
├── .gitignore
├── README.md
├── Postman_collection.json
└── backend/
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/fr/tp/meteo/
        │   │   ├── Application.java
        │   │   ├── controller/WeatherController.java
        │   │   ├── service/WeatherService.java
        │   │   └── dto/
        │   │       ├── GeocodingResponse.java
        │   │       ├── ForecastResponse.java
        │   │       └── Weather.java
        │   └── resources/application.properties
        └── test/java/fr/tp/meteo/service/WeatherServiceTest.java
~~~

## Lancer Spring Boot

Prérequis : JDK 17 ou supérieur, Maven, accès Internet.

Ouvrir un terminal dans le dossier backend :

~~~powershell
mvn clean test
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
~~~

Le serveur écoute sur http://localhost:8081 avec cette commande. Garder son terminal ouvert. Le port par défaut du fichier application.properties reste 8080 ; la commande le remplace pour ce lancement.

## Tester avec Postman avant Angular

Importer Postman_collection.json. Lancer les trois requêtes :

| Cas | Requête | Statut attendu |
| --- | --- | --- |
| Paris | GET http://localhost:8081/api/weather?city=Paris | 200 |
| Ville vide | GET http://localhost:8081/api/weather?city= | 400 |
| Ville inexistante | GET http://localhost:8081/api/weather?city=zzzzzzinconnue987654 | 404 |
| Paramètre absent | GET http://localhost:8081/api/weather | 400 |

Pour Paris, vérifier tous les champs du record Weather. Les valeurs changent avec la météo : ne pas comparer à des nombres fixes. Essayer aussi une ville contenant un espace ou un accent (Saint-Étienne).

Pour tester l'erreur de communication : couper temporairement Internet et rechercher Paris ; statut attendu 502, avec un message dans le champ detail. Rétablir Internet ensuite. Les erreurs sont au format Problem Details grâce à spring.mvc.problemdetails.enabled=true.

Les températures sont en °C, le vent en km/h, les précipitations en mm et les probabilités en %. Les valeurs quotidiennes concernent aujourd'hui dans le fuseau de la ville. count=1 sélectionne le premier résultat géographique : un nom ambigu peut correspondre à une autre ville homonyme.

## Comprendre le code

- Application démarre Spring Boot.
- WeatherController reçoit city et délègue au service, injecté par constructeur. CORS autorise http://localhost:4200.
- WeatherService valide la ville, appelle Geocoding, utilise les coordonnées pour appeler Forecast et construit Weather. Les délais de connexion et de lecture évitent une attente illimitée.
- GeocodingResponse et ForecastResponse représentent les JSON externes. Les annotations JsonProperty associent les noms Open-Meteo aux noms Java.
- Weather est un record Java 17 : une petite classe de données immuable dont Java génère les accesseurs. C'est le contrat JSON du futur frontend.
- WeatherServiceTest vérifie le rejet d'une ville vide ou nulle avant tout appel externe. Ce test ne valide pas les appels réels Open-Meteo.

Pas de base de données, de Repository, de JPA ou de sécurité supplémentaire.

## Lancer Angular

Prérequis : Node.js 22.12 ou supérieur dans la branche 22, ou Node.js 24 LTS. La version 22.11 détectée lors de la première session doit être mise à jour pour Angular 20. Aucun Angular CLI global n'est nécessaire.

Dans un deuxième terminal, ouvrir le dossier frontend :

~~~powershell
npm.cmd install
npm.cmd start
~~~

Ouvrir http://localhost:4200 et cliquer sur Rechercher. Laisser le backend et Angular actifs dans leurs deux terminaux.

Pour compiler : npm.cmd run build.

Le seul appel HTTP des données météo du navigateur vise http://localhost:8081/api/weather. Si le port du backend change, modifier apiUrl dans src/app/services/weather.service.ts.

Arborescence complète des fichiers frontend créés :

~~~text
frontend/
├── package.json
├── angular.json
├── tsconfig.json
├── tsconfig.app.json
└── src/
    ├── index.html
    ├── main.ts
    ├── styles.css
    └── app/
        ├── app.config.ts
        ├── app.component.ts
        ├── models/weather.ts
        ├── services/weather.service.ts
        └── weather/
            ├── weather.component.ts
            ├── weather.component.html
            └── weather.component.css
~~~

Après npm install, package-lock.json et node_modules sont générés. Le service Angular injecte HttpClient par constructeur ; le composant injecte WeatherService. FormsModule permet [(ngModel)], (ngSubmit) appelle search(), [disabled] évite les recherches simultanées et @if affiche les résultats ou les erreurs. provideHttpClient est configuré dans app.config.ts. Le composant utilise les codes météo actuels et quotidiens pour afficher un libellé simple.

Vérification complète à effectuer dans le navigateur : Paris affiche toutes les données ; une ville inexistante affiche le message du backend ; un champ vide affiche la validation locale ; backend arrêté, la recherche affiche une erreur de connexion. Dans les outils de développement, onglet Réseau, vérifier que la requête météo vise localhost:8081 et reçoit 200. La réponse montrée par l'utilisateur valide déjà les appels réels du backend ; le fonctionnement du frontend dans le navigateur reste à vérifier.

L'URL finale de l'application est http://localhost:4200 une fois npm.cmd start lancé.

Flux final prévu : Angular → WeatherController → WeatherService → API Geocoding → API Weather → WeatherService → WeatherController → Angular.

## Références consultées

- https://docs.spring.io/spring-boot/3.5/system-requirements.html
- https://angular.dev/reference/versions
- https://open-meteo.com/en/docs

## État des vérifications

Backend : cas 200, 400 et 404 confirmés par les réponses fournies par l'utilisateur.
Frontend : fichiers de configuration JSON contrôlés ; compilation et test navigateur non effectués, car le registre npm est bloqué par EACCES dans l'environnement Codex malgré l'autorisation réseau. Exécuter npm.cmd install, npm.cmd run build et npm.cmd start localement.
