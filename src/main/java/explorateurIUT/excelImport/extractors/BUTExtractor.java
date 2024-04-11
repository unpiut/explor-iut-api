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
package explorateurIUT.excelImport.extractors;

import explorateurIUT.excelImport.model.ExcelBUT;
import explorateurIUT.excelImport.model.ExcelParcoursBUT;
import java.util.Iterator;
import java.util.function.Consumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 *
 * @author Remi Venant
 */
public class BUTExtractor implements SheetExtractor<ExcelBUT> {

    private static final Log LOG = LogFactory.getLog(BUTExtractor.class);

    @Override
    public void extractEntities(XSSFSheet sheet, Consumer<ExcelBUT> entityConsumer) {
        LOG.debug("Loading excel sheet " + sheet.getSheetName());
        //Iterate over rows
        final Iterator<Row> itRows = sheet.rowIterator();
        //Skip first row (headings)
        itRows.next();

        //Iterate over remaining lines to complete IUT Info
        ExcelBUT currentBut = null;
        ExcelParcoursBUT currentParcours = null;

        while (itRows.hasNext()) {
            //Iterate cells
            final Iterator<Cell> itCells = itRows.next().cellIterator();
            if (!itCells.hasNext()) {
                LOG.debug("Having a row without any cells");
                continue;
            }
            while (itCells.hasNext()) {
                final Cell cell = itCells.next();
                String rawValue = ExtractorUtils.extractCellValue(cell);
                if (rawValue == null || rawValue.isBlank()) {
                    continue;
                }
                rawValue = rawValue.trim();
                final int columnIdx = cell.getAddress().getColumn();
                switch (columnIdx) {
                    case 0 -> { // BUT Code, new BUT
                        if (currentBut != null) {
                            entityConsumer.accept(currentBut);
                        }
                        LOG.debug("Create new BUT of name " + rawValue);
                        currentBut = new ExcelBUT(rawValue);
                    }
                    case 1 -> { // BUT Name
                        if (currentBut == null) {
                            LOG.warn("New BUT name cell with no current BUT: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            currentBut.setNom(rawValue);
                        }
                    }
                    case 2 -> { // BUT filiere metier
                        if (currentBut == null) {
                            LOG.warn("New BUT filiere metier cell with no current BUT: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            currentBut.setFiliere(rawValue);
                        }
                    }
                    case 3 -> { // BUT parcours
                        if (currentBut == null) {
                            LOG.warn("New BUT filiere metier cell with no current BUT: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            currentParcours = new ExcelParcoursBUT(rawValue);
                            currentBut.getParcours().add(currentParcours);
                        }
                    }
                    case 4 -> { // Parcours nom
                        if (currentParcours == null) {
                            LOG.warn("New Parcours nom cell with no current parcours: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            currentParcours.setNom(rawValue);
                        }
                    }
                    case 5 -> { // Parcours mot-clé : desactivé
//                        if (currentParcours == null) {
//                            LOG.warn("New Parcours mot-cle cell with no current parcours: " + cell.getAddress().formatAsR1C1String());
//                        } else {
//                            currentParcours.setMotsCles(rawValue.replaceAll("\\s*,\\s*", " "));
//                        }
                    }
                    case 6 -> { // BUT métiers
                        if (currentBut == null) {
                            LOG.warn("New BUT metiers cell with no current BUT: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            //currentParcours.setMetiers(Arrays.asList(rawValue.split("\\s*,\\s*")));
                            currentBut.setMetiers(rawValue);
                        }
                    }
                    case 7 -> { // BUT Description
                        if (currentBut == null) {
                            LOG.warn("New BUT description cell with no current BUT: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            currentBut.setDescription(rawValue);
                        }
                    }
                    case 8 -> { // BUT URL
                        if (currentBut == null) {
                            LOG.warn("New BUT url cell with no current BUT: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            currentBut.setUrlFiche(rawValue);
                        }
                    }
                    case 9 -> { // Lien france compétence
                        if (currentBut == null) {
                            LOG.warn("New BUT lien france compétence cell with no current BUT: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            currentBut.setUrlFranceCompetence(rawValue);
                        }
                    }
                    case 10 -> { // Univers métiers
                        if (currentBut == null) {
                            LOG.warn("New BUT univers métiers cell with no current BUT: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            currentBut.setUniversMetiers(rawValue);
                        }
                    }
                    default ->
                        LOG.debug("Outside scope cell of adress: " + cell.getAddress().formatAsR1C1String());
                }
                // Si on a atteind la 8ème colonne, on break la boucle de parcours des colonnes
                if (columnIdx >= 11) {
                    break;
                }
            }
        }

        if (currentBut != null) {
            entityConsumer.accept(currentBut);
        }
    }
}
