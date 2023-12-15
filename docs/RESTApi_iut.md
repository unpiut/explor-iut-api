# API REST du serveur : Reférentiel

## Résumés de IUT (sans filtrage)

__[GET] /api/v1/iut__

### Paramètres de requête d'URL

aucun

### Status possibles

- __200__ : Ok.
- __500__ : Erreur interne du serveur

### Structure de réponse (en cas de succès)

Tableau de résumés des IUT et de leurs parcours

```
[
  {
    "id": String, identifiant du site d'IUT.
    "nom": String, nom de l'IUT. Peut être commun à différent IUT quand un IUT est répartit sur plusieurs sites (lieux).
    "site": String, site (lieux) de l'IUT. À noter que certain IUT n'ont qu'un seul site, qui peut dans ce cas ne pas être nommé (valeur non existante ou nulle dans ce cas).
    "region": String, région de l'IUT.
    "location": {Coordonnées GeoJSON de l'IUT.
      "x": Double, latitude des coordonnées GPS de l'IUT.
      "y": Double, longitude des coordonnées GPS de l'IUT.
      "type": "Point": constante du type GeoJSON.
      "coordinates": Array<Double>, tableau des même coordonnées x et y.
    },
    "departements": [
      {
        "id": String, identifiant du département.
        "code": String, code du département. Unique au sein de l'IUT, peut-être multiple sur l'ensemble des département.
        "codesButDispenses": Array<String>, tableau des codes de BUT dispensés par le département.
      }, ...
    ]
  }, ...
]
```

### En-Têtes de retour particulier

Cette requête retourne les en-têtes de mise en cache navigateur _Cache-Control_ (max-age) et _ETag_ pour améliorer les performances d'accès coté client et serveur.

## Détails d'un IUT

__[GET] /api/v1/iut/{idIUT}__ \
avec comme paramètre d'URL __{idIUT}__ l'identifiant de l'IUT

### Paramètres de requête
_Aucun_

### Status possibles

- __200__ : Ok.
- __400__ : Requête invalide (paramètre d'URL au mauvais format)
- __404__ : IUT inconnu
- __500__ : Erreur interne du serveur

### Structure de réponse (en cas de succès)

Instance de l'IUT complète incluant ses departements, complet incluant eux-même leurs parcours avec les informations d'alternance. Pour le détail, consultez le [Readme](../README.md).


## Recherche de résumés d'IUT par critères

La recherche de résumés d'IUT par critères permet de cibler les IUT et les formations qu'ils proposent dans leurs départements selon 4 catégories de criètes, combinables :

- les métiers visé ;
- les formations proposées en alternance uniquement ou non ;
- la proximité géographique
- la correspondance à une recherche en "texte libre" (comme dans un moteur de recherche) 

Si la proximité géographique filtre directement les sites d'IUT, les autres catégories cibles des formations (des BUT) proposés dans les départements d'IUT. Aussi, ce service, par défaut, ne transmettra dans les résumés d'IUT, que les département et les codes formation qui correspondent au filtre. Il est possible de désactiver ce comportement.

L'ensemble de ces filtres et options sont utilisables à travers des paramètres de requête d'URL. Attention à encoder les valeurs en HTML pour éviter les URL invalide (ex.: "Chef de chantier Bâtiment" -> "Chef%20de%20chantier%20B%C3%A2timent")

__[GET] /api/v1/iut__

### Paramètres de requête d'URL

_Tous les paramètres ci-dessous sont optionnel, mais au moins un des filtre doit être renseigné. Autrement le comportement sera celui de l'obtention des Résumés de IUT sans filtrage._

- __job__ : nom de métier qui doit être cible d'au moins un parcours d'une formation de l'IUT (tient compte de la casse). Paramètre répétable jusqu'à 20 occurences. Si plusieurs métiers sont fournis, le service filtre les formation qui mènent à au moins un des métiers donnés (OU logique inclusif).
- __but__: filtrage par formation (code de BUT) proposé par l'IUT (tient compte de la casse). Paramètre répétable jusqu'à 20 occurences. Si plusieurs formations sont fournis, le service filtre les IUT qui proposent au moins une des formation données (OU logique inclusif). Ce paramètre est incompatible avec le paramètre block-but.
- __lat__: filtrage par proximité géographique. Latitude du centre du cercle de la zone de filtrage. Paramètre non répétable. Ce paramètre impose l'utilisation du paramètre lon pour rendre le filtrage par proximité géographique fonctionnel.
- __lon__: filtrage par proximité géographique. Longitude du centre du cercle de la zone de filtrage. Paramètre non répétable. Ce paramètre impose l'utilisation du paramètre lat pour rendre le filtrage par proximité géographique fonctionnel.
- __rad__: rayon de la zone de filtrage géographique (en km). Paramètre non répétable. Ce paramètre impose l'utilisation des paramètre lat et lon pour rendre le filtrage par proximité géographique fonctionnel. Si lat et lon sont fourni mais pas le paramètre rad, un rayon de 100km par défaut sera utilisé.
- __q__ : filtrage par recherche libre. Filtre les iut dont au moins un département propose un parcours de BUT dont le nom, les métiers ou les mots clés le caractérisant sont similaires au texte fourni. La casse n'est pas prise en compte, ni la diacritique (ne distingue pas les lettres accuentuées. Ex.: e = é = è = ê). La valeur doit être un texte quelconque. Paramètre non répétable.
- __all-depts__ : par défaut, le filtrage retourne des résumé d'IUT ne contenant que les départements et leurs codes de diplôme BUT proposés correspondant au filtres fournis. Si ce paramètre est présent et si la valeur associée n'est ni "no", ni "false", ni 0, ce comportement est désactivé et les résumés d'IUT retournés contiendront tous leurs résumés de départements, eux-même contenant tous les codes de formation (de BUT) proposés.
- __block-only__: Si ce paramètre est présent et si la valeur associée n'est ni "no", ni "false", ni 0, filtrage des IUT dont les formations selectionnées proposent au moins un parcours ouvert à l'alternance.

Tous les paramètre peuvent être combiné. Lorsque des paramètres différents sont combinés, le ET logique est appliqué. Des exemples sont fournis à la fin de cette section.


### Status possibles

- __200__ : Ok.
- __400__ : Requête invalide (paramètre de requête au mauvais format, ou utilisation incorrecte)
- __500__ : Erreur interne du serveur

### Structure de réponse (en cas de succès)

Tableau de résumés des IUT et de leurs parcours. Si all-depts a été activé en paramètre, seuls les codes but dispensé et les départements correspondant au filtre sont donnés dans les résumés d'IUT.

```
[
  {
    "id": String, identifiant du site d'IUT.
    "nom": String, nom de l'IUT. Peut être commun à différent IUT quand un IUT est répartit sur plusieurs sites (lieux).
    "site": String, site (lieux) de l'IUT. À noter que certain IUT n'ont qu'un seul site, qui peut dans ce cas ne pas être nommé (valeur non existante ou nulle dans ce cas).
    "region": String, région de l'IUT.
    "location": {Coordonnées GeoJSON de l'IUT.
      "x": Double, latitude des coordonnées GPS de l'IUT.
      "y": Double, longitude des coordonnées GPS de l'IUT.
      "type": "Point": constante du type GeoJSON.
      "coordinates": Array<Double>, tableau des même coordonnées x et y.
    },
    "departements": [
      {
        "id": String, identifiant du département.
        "code": String, code du département. Unique au sein de l'IUT, peut-être multiple sur l'ensemble des département.
        "codesButDispenses": Array<String>, tableau des codes de BUT dispensés par le département.
      }, ...
    ]
  }, ...
]
```

### Exemple de requêtes de filtrage

- __/api/v1/iut?job=Assistant%20R&D&job=Chef%20de%20chantier%20B%C3%A2timent__ : récupère tous les IUT proposant au moint une formation menant au métieur de Chef de chantier Bâtiment ou d'Assistant R&D. Les résumés d'IUT correspondant ne contiendront que les résumés de départements correspondant, eux-même contenant uniquement les codes de BUT dispensés correspondant.

- __/api/v1/iut?job=Assistant%20R&D&job=Chef%20de%20chantier%20B%C3%A2timent&all-depts__ : récupère tous les IUT proposant au moint une formation menant au métieur de Chef de chantier Bâtiment ou d'Assistant R&D. Les résumés d'IUT correspondant contiendront tous leurs résumés de départements, eux-même contenant tous les codes de BUT dispensés.

- __/api/v1/iut?but=MMI&but=INFO__ : récupère tous les IUT dont au moins un département propose la formation MMI ou la formation INFO (que l'alternance y soit proposée ou non). Les résumés d'IUT correspondant ne contiendront que les résumés de départements correspondant, eux-même contenant uniquement les codes de BUT dispensés correspondant.

- __/api/v1/iut?block-but=MMI&but=INFO__ : récupère tous les IUT dont au moins un département propose la formation MMI ou la formation INFO, avec au moins un parcours ouvert à l'alternance. Les résumés d'IUT correspondant ne contiendront que les résumés de départements correspondant, eux-même contenant uniquement les codes de BUT dispensés correspondant.

- __/api/v1/iut?lat=43.21813458878689&lon=2.351731401607915__ : récupère tous les IUT proches de Caracassone, dans un rayon de 100km. Comme ce filtre ne s'applique qu'aux IUT, Les résumés d'IUT correspondant contiendront tous leurs résumés de départements, eux-même contenant tous les codes de BUT dispensés.

- __/api/v1/iut?lat=48.11126260367909&lon=-1.6781945224303658&rad=200__ : récupère tous les IUT proches de Rennes, dans un rayon de 200km. Comme ce filtre ne s'applique qu'aux IUT, Les résumés d'IUT correspondant contiendront tous leurs résumés de départements, eux-même contenant tous les codes de BUT dispensés.

- __/api/v1/iut?q=html%20css%20javascript__ : récupère tous les IUT dont au moins un des parcours de formation proposé dans l'un des département correspond aux termes "html", "css" ou "javascript" (dans son nom, ses mots-clés ou ses métiers). Les résumés d'IUT correspondant ne contiendront que les résumés de départements correspondant, eux-même contenant uniquement les codes de BUT dispensés correspondant.

- __/api/v1/iut?block-but=MMI&lat=48.11126260367909&lon=-1.6781945224303658&rad=300&q=JavaScript__ : récupère tous les IUT dans les 300km autour de Rennes, ET dont au moins un département propose la formation MMI avec au moins un parcours ouvert à l'alternance, ET dont ce parcours correspon au terme "javascript" (dans son nom, ses mots-clés ou ses métiers). Les résumés d'IUT correspondant ne contiendront que les résumés de départements correspondant, eux-même contenant uniquement les codes de BUT dispensés correspondant.
