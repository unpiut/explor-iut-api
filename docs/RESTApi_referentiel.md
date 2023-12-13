# API REST du serveur : Reférentiel

## Résumés des BUT

__[GET] /api/v1/referentiel/but__

### Paramètres de requête d'URL
_Aucun_

### Status possibles

- __200__ : Ok.
- __500__ : Erreur interne du serveur

### Structure de réponse (en cas de succès)

Tableau de résumés de BUT et de leurs parcours

```
[
  {
    "id": String, identifiant du diplôme.
    "code": String, code du diplôme (unique parmi les diplômes).
    "nom": String, nom complét du diplôme.
    "filiere": String, filière de métiers visée.
    "parcours": [
      {
        "id": String, identifiant du parcours.
        "code": String, code du parcours (unique parmi les parcours).
        "nom": String, nom complet du parcours.
      }, ...
    ]
  }, ...
]
```

### En-Têtes de retour particulier

Cette requête retourne les en-têtes de mise en cache navigateur _Cache-Control_ (max-age) et _ETag_ pour améliorer les performances d'accès coté client et serveur.

## Détails d'un BUT

Deux points d'accès possibles :

__[GET] /api/v1/referentiel/but/{idBut}__ \
avec comme paramètre de chemin d'URL __{idBut}__ l'identifiant du but

__[GET] /api/v1/referentiel/but/by-code/{codeBut}__ \
avec comme paramètre de chemin d'URL __{codeBut}__ le code du but (ex.: MMI). La casse n'est pas prise en compte (ex.: MMI = mmi = Mmi)

### Paramètres de requête d'URL
_Aucun_

### Status possibles

- __200__ : Ok.
- __400__ : Requête invalide (paramètre d'URL au mauvais format)
- __404__ : BUT inconnu
- __500__ : Erreur interne du serveur

### Structure de réponse (en cas de succès)

Instance du BUT complète incluant ses parcours, complets également. Pour le détail, consultez le [Readme](../README.md).

## Métiers par filière

__[GET] /api/v1/referentiel/metiers__

### Paramètres de requête d'URL
_Aucun_

### Status possibles

- __200__ : Ok.
- __500__ : Erreur interne du serveur

### Structure de réponse (en cas de succès)

Tableau de métiers regroupés par filières existant sur l'ensemble des BUT et de leurs parcours

```
[
  {
    "filiere": String, filière métier, présente sur au moins un BUT.
    "metiers": Array<String>, tableau de métiers appartenant à la filière (chaque occurence est garantie d'être unique dans le tableau).
  }, ...
]
```

### En-Têtes de retour particulier

Cette requête retourne les en-têtes de mise en cache navigateur _Cache-Control_ (max-age) et _ETag_ pour améliorer les performances d'accès coté client et serveur.

