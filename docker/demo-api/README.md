# Pile Docker de démonstration de l'API

## Prérequis

- Docker avec Docker compose
- Un dossier local contenant au moins un fichier .xlsx de donnée ExplorIUT prefixé par "data". Il est conseillé de ne pas avoir dans ce dossier d'autre fichier que celui des données initialement.

## Mise en place

Editez le fichier __docker-compose.yml__ :

- l. 37 : modifiez la variable d'environnement VALIDATION_URL pour pointer sur l'url de votre serveur d'application front pour le dev., le cas échéant.
- l. 47 : modifiez le dossier hôte du bind mount "/repertoire-donnees" par le chemin vers votre dossier comportant votre fichier de données. Conservez un montage en lecture/éciture.

## Lancement

Simple commande docker compose :

```
docker compose up -d
``` 

## Accès à l'API rest

Par défaut l'api est accessible depuis l'hôte sur http://localhost:8080 (ex. : http://localhost:8080/api/v1/textes)

## Accès au serveur SMTP de mail de test

Par défaut l'application web de gestion du serveur SMTP de test est accessible depuis l'hôte sur http://localhost:1080

## Accès au client mongo

Commande docker compose à exécuter telle quelle :

```
docker compose exec -ti nosqldatabase mongosh
```

