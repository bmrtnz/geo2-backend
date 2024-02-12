[![status-badge](https://ci.microtec.fr/api/badges/Microtec/geo2-backend/status.svg?branch=master)](https://ci.microtec.fr/Microtec/geo2-backend)

# Geo2-Backend

Geo2 api application.

## Technologies

-   Java 11
-   Spring Boot 2.x
-   PostgresQL (Oracle 9i at this time)
-   GraphQL

## SQL

Les scripts SQL sont localisés dans le dossier `sql` et beneficient du systeme de versionnage du projet.

Pour faciliter le deploiement de ceux ci, un script de fusion est disponible (`.sql/compile`).

Par exemple, la commande `./sql/compile tags/1.18.2` generera un script SQL avec l'ensemble des fichiers modifiés depuis le tag _1.18.2_ par rapport à l'entete actuelle.
