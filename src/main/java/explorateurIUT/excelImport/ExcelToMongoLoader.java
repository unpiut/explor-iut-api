/*
 * Copyright (C) 2023 IUT Laval - Le Mans Université.
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
package explorateurIUT.excelImport;

import explorateurIUT.excelImport.model.ExcelAnneeAlt;
import explorateurIUT.excelImport.model.ExcelBUT;
import explorateurIUT.excelImport.model.ExcelDepartement;
import explorateurIUT.excelImport.model.ExcelIUT;
import explorateurIUT.excelImport.model.ExcelParcoursBUT;
import explorateurIUT.excelImport.model.ExcelParcoursDeptDip;
import explorateurIUT.model.Alternance;
import explorateurIUT.model.BUT;
import explorateurIUT.model.Departement;
import explorateurIUT.model.IUT;
import explorateurIUT.model.ParcoursBUT;
import explorateurIUT.model.ParcoursDept;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Query;

/**
 *
 * @author Remi Venant
 */
public class ExcelToMongoLoader {

    private static final Log LOG = LogFactory.getLog(ExcelToMongoLoader.class);

    private final MongoTemplate mongoTemplate;

    private final HashMap<String, BUT> butByCode = new HashMap<>();
    private final HashMap<String, ParcoursBUT> parcoursButByCodeButParc = new HashMap<>();

    public ExcelToMongoLoader(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void reset() {
        this.butByCode.clear();
        this.parcoursButByCodeButParc.clear();
    }

    public void clearDb() {
        final Query matchAll = new Query();
        Stream.of(ParcoursDept.class, Departement.class, IUT.class, ParcoursBUT.class, BUT.class)
                .forEach((c) -> this.mongoTemplate.remove(matchAll, c));
    }

    public Consumer<ExcelBUT> getExcelBUTConsumer() {
        return new BUTConsumer();
    }

    public Consumer<ExcelIUT> getExcelIUTConsumer() {
        return new IUTConsumer();
    }

    public class BUTConsumer implements Consumer<ExcelBUT> {

        @Override
        public void accept(ExcelBUT excelBut) {
            //Create an instance of BUT then all instance of parcours
            final BUT but = mongoTemplate.save(butFromExcel(excelBut));
            butByCode.put(but.getCode(), but);
            excelBut.getParcours().forEach((excelParcours) -> {
                ParcoursBUT parcours = mongoTemplate.save(parcoursFromExcel(but, excelParcours));
                String key = but.getCode() + "_" + parcours.getCode();
                parcoursButByCodeButParc.put(key, parcours);
            });
        }
    }

    public class IUTConsumer implements Consumer<ExcelIUT> {

        @Override
        public void accept(ExcelIUT excelIut) {
            // Start creating an instance of IUT
            final IUT iut = mongoTemplate.save(iutFromExcel(excelIut));
            excelIut.getDepartements().forEach((excelDept) -> {
                final Departement dept = mongoTemplate.save(deptFromExcel(iut, excelDept));
                excelDept.getDiplomes().stream()
                        .flatMap((ed) -> ed.getParcours().stream()
                        .map((ep) -> new AbstractMap.SimpleEntry<String, ExcelParcoursDeptDip>(ed.getCode(), ep)))
                        .forEach((entry) -> {
                            // Récupération du parcours but
                            final String butParcKey = entry.getKey() + "_" + entry.getValue().getCode();
                            final ParcoursBUT parcours = parcoursButByCodeButParc.get(butParcKey);
                            if (parcours == null) {
                                LOG.warn("Parcours not found for key " + butParcKey);
                                return;
                            }
                            // maj dept avec but
                            dept.addButDispense(parcours.getBut());
                            // Creation alternances du parcours
                            final List<Alternance> alternances = entry.getValue().getAnneesAlt().stream()
                                    .map(ExcelToMongoLoader::alternanceFromExcel).toList();
                            // Creation et sauvegarde du parcours
                            mongoTemplate.save(parcoursDeptFromExcel(iut, dept, parcours, alternances));
                        });
                // update dept since but dispensé have been added
                mongoTemplate.save(dept);
            });
        }
    }

    private static BUT butFromExcel(ExcelBUT but) {
        return new BUT(but.getCode(), but.getFiliere(), but.getDescription(), but.getUrlFiche());
    }

    private static ParcoursBUT parcoursFromExcel(BUT but, ExcelParcoursBUT parcours) {
        return new ParcoursBUT(but, parcours.getCode(), parcours.getNom(), parcours.getMotsCles(), parcours.getMetiers());
    }

    private static IUT iutFromExcel(ExcelIUT iut) {
        GeoJsonPoint gpsCoor = null;
        if (iut.getCoorGpsLat() != null && iut.getCoorGpsLon() != null) {
            gpsCoor = new GeoJsonPoint(iut.getCoorGpsLat(), iut.getCoorGpsLon());
        }
        return new IUT(iut.getNom(), iut.getVille(), iut.getAdresse(), iut.getTel(), iut.getMel(), iut.getUrl(), gpsCoor);
    }

    private static Departement deptFromExcel(IUT iut, ExcelDepartement dept) {
        return new Departement(iut, dept.getCode(), dept.getTel(), dept.getMel(), dept.getUrl());
    }

    private static ParcoursDept parcoursDeptFromExcel(IUT iut, Departement dept, ParcoursBUT parcoursBut, List<Alternance> alternances) {
        return new ParcoursDept(iut, dept, parcoursBut, alternances);
    }

    private static Alternance alternanceFromExcel(ExcelAnneeAlt alt) {
        return new Alternance(alt.getAnnee(), alt.getMel(), alt.getTel(), alt.getContact(), alt.getUrlCal());
    }
}
