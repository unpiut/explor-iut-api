# Serveur OpenData de l'Explorateur d'IUT

Ce serveur expose une API REST exposant à la fois le référentiel sommaire des BUT (uniquement les structures de BUT et de Parcours, mais pas les structures de Compétences),
et les structures d'IUT en france (IUT, département, diplômes et parcours proposés), leurs informations de contacts et les informations relative à l'alternance.

## Modèle de données exposé

Ce serveur expose un modèle de données orienté document. Selon les services invoqués, les informations peuvent être plus ou moins détaillées (se reporter à l'API REST pour connaitre les extraits de modèle fourni en fonction du service invoqué). Est présenté ici le modèle exposé le plus détaillé

### Référentiel

#### BUT

```
{
  "id": String, identifiant du diplôme.
  "code": String, code du diplôme (unique parmi les diplômes).
  "nom": String, nom complét du diplôme.
  "filiere": String, filière de métiers visée.
  "description": String, texte court de description du diplôme.
  "urlFiche": String, URL de la fiche de présentation officielle du diplôme.
  "parcours": Tableau de Parcours de BUT.
}
```

#### Parcours de BUT

```
{
  "id": String, identifiant du parcours.
  "code": String, code du parcours (unique parmi les parcours).
  "nom": String, nom complet du parcours.
  "metiers": Array<String>, tableau de métiers visés par le parcours. À noter que l'ensemble des parcours d'un même diplôme vise globalement les mêmes métiers, mais avec un "ordre de préférence" possiblement différent.
}
```

### Structures d'IUT

#### Site d'IUT

Un IUT pouvant être répartit sur plusieurs sites (lieux) l'unité ici est le site d'IUT.

```
{
  "id": String, identifiant du site d'IUT.
  "nom": String, nom de l'IUT. Peut être commun à différent IUT quand un IUT est répartit sur plusieurs sites (lieux).
  "site": String, site (lieux) de l'IUT. À noter que certain IUT n'ont qu'un seul site, qui peut dans ce cas ne pas être nommé (valeur non existante ou nulle dans ce cas).
  "address": String, adresse postale du site d'IUT.
  "tel": String, numéro de téléphone de l'accueil général de l'IUT (le plus souvent le service de scolarité ou le secrétariat principal).
  "mel": String, adresse mél de contact principale de l'IUT.
  "urlWeb": String, adresse du site web de l'IUT.
  "location": { Coordonnées GeoJSON de l'IUT.
    "x": Double, latitude des coordonnées GPS de l'IUT.
    "y": Double, longitude des coordonnées GPS de l'IUT.
    "type": "Point": constante du type GeoJSON.
    "coordinates": Array<Double>, tableau des même coordonnées x et y.
  },
  "departements": Array<Departement>, tableau des départements de l'IUT.
}
```

#### Département d'IUT

Un Département dispense générale un seul diplôme BUT, mais rien n'empêche théoriquement d'en dispenser plusieurs.

```
{
  "id": String, identifiant du département.
  "code": String, code du département. Unique au sein de l'IUT, peut-être multiple sur l'ensemble des département.
  "tel": String, numéro de téléphone du département.
  "mel": String, adresse mél du secrétariat du département.
  "urlWeb": String, URL du site web du déparement ou de la page du département sur le site web de son IUT de rattachement.
  "codesButDispenses": Array<String>, tableau des codes de BUT dispensés par le département.
  "parcours": Array<Parcours de département>, tableau des parcours de BUT dispensé par le département (n'existe que parmi les BUT dispensés par ce dernier).
}
```

#### Parcours de département

```
{
  "id": String, identifiant du parcours d'IUT.
  "codeParcours": String, code du parcours BUT.
  "alternances": Array<Information d'alternance> informations sur l'alternance proposée dans ce parcours au sein de ce département. Peut être inexistant, null ou vide.
}
```

#### Information d'alternance de parcours de département

```
{
  "annee": Integer, année du diplôme concernée par ces information d'alternance.
  "mel": String, adresse mail du contact pour cette alternance. Peut être inexistant ou null.
  "tel": String, numéro de téléphone du contact pour cette alternance. Peut être inexistant ou null.
  "contact": String, identité du contact pour cette alternance. Peut être inexistant ou null.
  "urlCalendrier": String, URL du calendrier de l'alternance. Peut être inexistant ou null.
}
```

## API REST

L'API REST est divisé en deux catégories :

- [le référentiel](docs/RESTApi_referentiel.md) : permet d'accéder aux informations relatives au BUT, à ses parcours et aux métiers
- [les IUT](docs/RESTApi_iut.md) : permet d'accéder aux informations relatives aux IUT à leurs départements, les parcours que ces derniers proposent et leurs informations sur l'alternance proposée