/*
 * Copyright (C) 2024 IUT Laval - Le Mans Université.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package explorateurIUT.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.BasicQuery;

/**
 *
 * @author Remi Venant
 */
public class TestDatasetGenerator {

    private static final Log LOG = LogFactory.getLog(TestDatasetGenerator.class);

    private final MongoTemplate mongoTemplate;

    private boolean init;

    private TestInstances testInstances;

    public TestDatasetGenerator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public boolean isInit() {
        return this.init;
    }

    public TestInstances getTestInstances() {
        return this.init ? this.testInstances : null;
    }

    public void clear() {
        LOG.info("Clear all test dataset in db");
        this.testInstances = null;
        this.mongoTemplate.remove(new BasicQuery("{}"), Departement.class);
        this.mongoTemplate.remove(new BasicQuery("{}"), IUT.class);
        this.mongoTemplate.remove(new BasicQuery("{}"), ParcoursBUT.class);
        this.mongoTemplate.remove(new BasicQuery("{}"), BUT.class);
        this.init = false;
    }

    public void createDataset() {
        LOG.info("INIT TEST DATASET");
        if (this.init) {
            this.clear();
        }
        this.testInstances = new TestInstances();
        this.createBUT();
        this.createIUT();
        this.init = true;
        LOG.info("TEST DATASET READY");
    }

    private void createBUT() {
        BUT but = this.mongoTemplate.save(new BUT("GB", "Génie Biologique (GB)", "Métiers Biologie-Nutrition-Agronomie",
                "en Agricole (exploitant, conseillé agricole, technico-commercial,..),  en industrie IAA (technicien production, assistant qualité, technicien laboratoire microbiologie, traitement déchets et pollution en milieu industriel, naturel ou urbain), en santé : diététicien, technico-commercial, Labo médicaux.",
                "Développe les compétences permettant de travailler dans tous les secteurs qui touchent  les connaissances de la biologie. 5 spécialités proposée : Nutrition, Biochimie(Labo, R&D) , Biologie Médicale, Science des aliments (Prod IAA) , Agronomie, Environnement (pollution, gestion espaces naturels ou urbain,...)",
                "https://www.iut.fr/bachelor-universitaire-de-technologie/genie-biologique/",
                "https://www.iut.fr/bachelor-universitaire-de-technologie/genie-biologique/",
                "Métiers Industriels : Prod-Maintenance, Qualité-R&D"));
        but.setParcours(List.of(
                this.mongoTemplate.save(new ParcoursBUT(but, "AGRO", "Agronomie")),
                this.mongoTemplate.save(new ParcoursBUT(but, "BMB", "Biologie Médicale et Biotechnologie")),
                this.mongoTemplate.save(new ParcoursBUT(but, "DN", "Diététique et Nutrition")),
                this.mongoTemplate.save(new ParcoursBUT(but, "SAB", "Sciences de l’Aliment et Biotechnologie")),
                this.mongoTemplate.save(new ParcoursBUT(but, "SEE", "Sciences de l’environnement et écotechnologies"))));
        this.testInstances.butGB = but;

        but = this.mongoTemplate.save(new BUT("MMI", "Métiers du Multimédia et de l'Internet MMI", "Métiers du Digital et du Web",
                "intégrateur, développeur et rédacteur web, intégrateur de dispositifs de réalité virtuelle, UX desingner, référencement internet",
                "Développe les compétences permettant de concevoir, développer, gérer des sites internet,  d’animer et d’entretenir  la communication multimédia par des productions graphiques, audio ou vidéo",
                "https://www.iut.fr/bachelor-universitaire-de-technologie/metiers-du-multimedia-et-de-linternet/",
                "https://www.iut.fr/bachelor-universitaire-de-technologie/metiers-du-multimedia-et-de-linternet/",
                "Métiers de l'infomatique"));
        but.setParcours(List.of(
                this.mongoTemplate.save(new ParcoursBUT(but, "DEVWEBDI", "Développement web et dispositifs interactifs")),
                this.mongoTemplate.save(new ParcoursBUT(but, "CREA", "Création Numérique")),
                this.mongoTemplate.save(new ParcoursBUT(but, "STRATUX", "Stratégie de communication numérique et design d’expérience"))));
        this.testInstances.butMMI = but;

        but = this.mongoTemplate.save(new BUT("INFO", "Informatique (Info)", "Métiers de l'informatique des Logiciels",
                "Développeur applications, intégrateur Progiciel/ERP, administrateur Réseaux/système et  de bases de données",
                "Développe les compétences permettant  de concevoir, développer, valider, installer, maintenir les solutions informatiques. Développeur ou chef de projet pour des applicatifs spécifiques ou  sous progiciel ou ERP.",
                "https://www.iut.fr/bachelor-universitaire-de-technologie/informatique-2/",
                "https://www.iut.fr/bachelor-universitaire-de-technologie/informatique-2/",
                "Métiers de l'infomatique"));
        but.setParcours(List.of(
                this.mongoTemplate.save(new ParcoursBUT(but, "RACDV", "Réalisation d’applications : conception, développement, validation")),
                this.mongoTemplate.save(new ParcoursBUT(but, "DACS", "Déploiement d’applications communicantes et sécurisées ")),
                this.mongoTemplate.save(new ParcoursBUT(but, "AGED", "Administration, gestion et exploitation des données")),
                this.mongoTemplate.save(new ParcoursBUT(but, "IAMSI", "Intégration d’applications et management du système d’information"))));
        this.testInstances.butINFO = but;
    }

    private void createIUT() {
        IUT iut = this.mongoTemplate.save(new IUT("IUT LAVAL", "Site Laval", "PAYS DE LOIRE",
                "52 Rue des Docteurs Calmette et Guérin, 53000 Laval", "02 43 59 49 01",
                "iut-laval@univ-lemans.fr", "https://iut-laval.univ-lemans.fr/fr/index.html",
                new GeoJsonPoint(48.08592681312958, -0.7570580033540599)));
        iut.setDepartements(List.of(
                this.mongoTemplate.save(this.createDeptWithParcours(iut, "Dept-Laval-MMI", List.of(
                        this.findParcoursInBUT(this.testInstances.butMMI, "DEVWEBDI"),
                        this.findParcoursInBUT(this.testInstances.butMMI, "CREA"),
                        this.findParcoursInBUT(this.testInstances.butMMI, "STRATUX")
                ))),
                this.mongoTemplate.save(this.createDeptWithParcours(iut, "Dept-Laval-INFO", List.of(
                        this.findParcoursInBUT(this.testInstances.butINFO, "RACDV"),
                        this.findParcoursInBUT(this.testInstances.butINFO, "DACS")
                ))),
                this.mongoTemplate.save(this.createDeptWithParcours(iut, "Dept-Laval-GB", List.of(
                        this.findParcoursInBUT(this.testInstances.butGB, "BMB")
                ))))
        );
        this.testInstances.iutLaval = iut;

        iut = this.mongoTemplate.save(new IUT("IUT LANNION", "Site Lannion", "BRETAGNE",
                "Rue Edouard Branly 22300 Lannion", "02 96 46 93 00",
                null, "https://iut-lannion.univ-rennes.fr/",
                new GeoJsonPoint(48.758786740771804, -3.4517647524795136)));
        iut.setDepartements(List.of(
                this.mongoTemplate.save(this.createDeptWithParcours(iut, "Dept-Lannion-MMI", List.of(
                        this.findParcoursInBUT(this.testInstances.butMMI, "DEVWEBDI"),
                        this.findParcoursInBUT(this.testInstances.butMMI, "STRATUX")
                ))),
                this.mongoTemplate.save(this.createDeptWithParcours(iut, "Dept-Lannion-INFO", List.of(
                        this.findParcoursInBUT(this.testInstances.butINFO, "RACDV"),
                        this.findParcoursInBUT(this.testInstances.butINFO, "AGED")
                ))))
        );
        this.testInstances.iutLannion = iut;
    }

    private Departement createDeptWithParcours(IUT iut, String code, Collection<ParcoursBUT> parcours) {
        Departement dept = new Departement(iut, code);
        HashMap<String, ButAndParcoursDispenses> parcoursDispensesByCodeBut = new HashMap<>();
        parcours.forEach((p) -> {
            ButAndParcoursDispenses parcoursDispense
                    = parcoursDispensesByCodeBut.computeIfAbsent(
                            p.getBut().getCode(),
                            (butCode) -> new ButAndParcoursDispenses(butCode, new HashSet<>()));
            parcoursDispense.addParcours(p);
        });
        dept.setButDispenses(new HashSet<>(parcoursDispensesByCodeBut.values()));
        return dept;
    }

    private ParcoursBUT findParcoursInBUT(BUT but, String parcoursCode) {
        return but.getParcours().stream().filter((p) -> p.getCode().equals(parcoursCode))
                .findFirst().orElseThrow(() -> new NoSuchElementException("No parcours " + parcoursCode + " in but " + but.getCode()));
    }

    public static class TestInstances {

        private BUT butGB, butMMI, butINFO;
        private IUT iutLaval, iutLannion;

        public BUT getButGB() {
            return butGB;
        }

        public BUT getButMMI() {
            return butMMI;
        }

        public BUT getButINFO() {
            return butINFO;
        }

        public IUT getIutLaval() {
            return iutLaval;
        }

        public IUT getIutLannion() {
            return iutLannion;
        }
    }
}
