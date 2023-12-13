# Collection: IUT
{
  _id
  nom: String,
  site: String,
  addresse: String,
  tel: String+pattern,
  mel: String+pattern,
  urlWeb: String+pattern,
  coordonnees: [lat: Number, lon: Number]
}

# Collection: Departements
{
  _id:
  iut: ObjectId
  code: String
  tel: String+pattern,
  mel: String+pattern,
  urlWeb: String+pattern,
}

# Collection: ParcoursDept
{
    _id
    IUT: ObjectId
    departement: ObjectId
    paroursBUT: ObjectId
    parcoursBUTInfo: {
        code: String,
        nom: String
    }
    alternance: [
        {
            annee: number
            mel: String+pattern,
            tel: String+pattern,
            contact: String,
            urlCalendrier: String+pattern
        }
    ]
}

## Collection: BUT
{
    _id
    code: String
    filiere: String
    description: String,
    urlFiche: String+pattern
}

## Collection: ParcoursBUT
{
    _id
    but: ObjectId
    code: String
    nom: String,
    motsCles: String
    metiers: [String]
}
