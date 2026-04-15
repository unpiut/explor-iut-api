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
package explorateurIUT.services.butIUTModelMgmt.excelToMemoryConsumers;

import explorateurIUT.services.butIUTModelMgmt.excelImport.model.ExcelDepartement;
import explorateurIUT.services.butIUTModelMgmt.excelImport.model.ExcelIUT;
import explorateurIUT.model.ButAndParcoursDispenses;
import explorateurIUT.model.Departement;
import explorateurIUT.model.GeoJsonPoint;
import explorateurIUT.model.IUT;
import explorateurIUT.model.ParcoursBUT;
import explorateurIUT.services.butIUTModelMgmt.BUTIUTModel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Remi Venant
 */
public class IUTConsumer implements Consumer<ExcelIUT> {

    private static final Log LOG = LogFactory.getLog(IUTConsumer.class);

    private final BUTIUTModel model;
    private final HashMap<String, ParcoursBUT> parcoursButByCodeButParc;

    public IUTConsumer(BUTIUTModel model, HashMap<String, ParcoursBUT> parcoursButByCodeButParc) {
        this.model = model;
        this.parcoursButByCodeButParc = parcoursButByCodeButParc;
        LOG.info("Create IUT COnsumer with parcoursButByCodeButParc: " + this.parcoursButByCodeButParc.keySet().toString());
    }

    @Override
    public void accept(ExcelIUT excelIut) {
        LOG.debug("Save IUT " + excelIut.getNom() + " - " + excelIut.getSite() + " -> " + excelIut.getCoorGpsLat() + " ; " + excelIut.getCoorGpsLon());
        // Start creating an instance of IUT
        final IUT iut = model.saveIUT(iutFromExcel(excelIut));
        excelIut.getDepartements().forEach((excelDept) -> {
            final Departement dept = deptFromExcel(iut, excelDept);
            final HashSet<ButAndParcoursDispenses> butAndParcoursDispenses = new HashSet<>();
            excelDept.getParcoursByDiplomes().entrySet().forEach(e -> {
                final String codeBut = e.getKey();
                final ButAndParcoursDispenses bpd = new ButAndParcoursDispenses(codeBut);
                e.getValue().forEach((String codeParcours) -> {
                    final String internalKey = codeBut + "_" + codeParcours;
                    final ParcoursBUT parcours = this.parcoursButByCodeButParc.get(internalKey);
                    if (parcours == null) {
                        LOG.warn(excelIut.getNom() + " - " + excelIut.getSite() + ": No parcours but found for parcours code " + codeParcours + " (internal key: " + internalKey + ")");
                    } else if (!parcours.getBut().getCode().equals(codeBut)) {
                        LOG.warn(String.format("Mismatch but code for parcours %s <> %s", codeBut, parcours.getBut().getCode()));
                    } else {
                        bpd.addParcours(parcours);
                    }
                });
                if (bpd.hasParcours()) {
                    butAndParcoursDispenses.add(bpd);
                }
            });
            dept.setButDispenses(butAndParcoursDispenses);
            model.saveDepartement(dept);
        });
    }

    private static IUT iutFromExcel(ExcelIUT excelIut) {
        GeoJsonPoint gpsCoor = null;
        if (excelIut.getCoorGpsLat() != null && excelIut.getCoorGpsLon() != null) {
            gpsCoor = new GeoJsonPoint(excelIut.getCoorGpsLon(), excelIut.getCoorGpsLat());
        }
        return new IUT(excelIut.getNom(), excelIut.getSite(), excelIut.getRegion(),
                excelIut.getAdresse(), excelIut.getTel(), excelIut.getMel(), excelIut.getUrl(), gpsCoor);
    }

    private static Departement deptFromExcel(IUT iut, ExcelDepartement excelDept) {
        Departement departement = new Departement(iut, excelDept.getCode());
        Set<ButAndParcoursDispenses> butAndParcoursDispenses = excelDept.getParcoursByDiplomes().entrySet()
                .stream()
                .map((e) -> new ButAndParcoursDispenses(e.getKey(), new HashSet<>(e.getValue())))
                .collect(Collectors.toSet());
        departement.setButDispenses(butAndParcoursDispenses);
        return departement;
    }

}
