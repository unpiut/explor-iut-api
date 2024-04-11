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

import static explorateurIUT.excelImport.extractors.ExtractorUtils.extractCellValue;
import static explorateurIUT.excelImport.extractors.ExtractorUtils.isComment;
import explorateurIUT.excelImport.formatters.BasicStringFormater;
import explorateurIUT.excelImport.formatters.EmailFormater;
import explorateurIUT.excelImport.formatters.GPSCoordinateFormater;
import explorateurIUT.excelImport.formatters.GPSDoubleCoordinateFormater;
import explorateurIUT.excelImport.formatters.TelFormater;
import explorateurIUT.excelImport.formatters.UrlFormater;
import explorateurIUT.excelImport.model.ExcelDepartement;
import explorateurIUT.excelImport.model.ExcelIUT;
import java.util.Iterator;
import java.util.List;
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
public class IUTExtractor implements SheetExtractor<ExcelIUT> {

    private static final Log LOG = LogFactory.getLog(IUTExtractor.class);

    @Override
    public void extractEntities(XSSFSheet sheet, Consumer<ExcelIUT> entityConsumer) {
        LOG.debug("Loading excel sheet " + sheet.getSheetName());
        //Iterate over rows
        final Iterator<Row> itRows = sheet.rowIterator();
        //Skip first row (headings)
        itRows.next();

        //Iterate over remaining lines to complete IUT Info
        ExcelIUT currentIUT = null;
        ExcelDepartement currentDept = null;
        String currentDiplomeCode = null;

        while (itRows.hasNext()) {
            //Iterate cells
            final Iterator<Cell> itCells = itRows.next().cellIterator();
            if (!itCells.hasNext()) {
                LOG.debug("Having a row without any cells");
                continue;
            }
            while (itCells.hasNext()) {
                final Cell cell = itCells.next();
                String rawValue = extractCellValue(cell);
                if (rawValue == null || rawValue.isBlank()) {
                    continue;
                }
                rawValue = rawValue.trim();
                //Skipp cell if content is comment or belonging to black list
                if (isComment(rawValue)) {
                    continue;
                }
                final int columnIdx = cell.getAddress().getColumn();
                switch (columnIdx) {
                    case 0 -> { // new IUT (name)
                        if (currentIUT != null) {
                            entityConsumer.accept(currentIUT);
                            currentDept = null;
                            currentDiplomeCode = null;
                        }
                        LOG.debug("Create new IUT of name " + rawValue);
                        currentIUT = new ExcelIUT(rawValue);
                    }
                    case 1 -> {// IUT Site (should create a new IUT if city already defined
                        if (currentIUT == null) {
                            LOG.warn("IUT town cell with no current IUT: " + cell.getAddress().formatAsR1C1String());
                        } else if (currentIUT.getSite() == null) {
                            currentIUT.setSite(rawValue);
                        } else {
                            String iutName = currentIUT.getNom();
                            entityConsumer.accept(currentIUT);
                            LOG.debug("Create new IUT of name with new city " + iutName);
                            currentIUT = new ExcelIUT(iutName);
                            currentIUT.setSite(rawValue);
                            currentDept = null;
                            currentDiplomeCode = null;
                        }
                    }
                    case 2 -> { // IUT Région
                        if (currentIUT == null) {
                            LOG.warn("IUT Region cell with no current IUT: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            currentIUT.setRegion(rawValue);
                        }
                    }
                    case 3 -> { // IUT Info (tel, adr, contact, url, coordinates over 1 or 2 lines)
                        if (currentIUT == null) {
                            LOG.warn("IUT info cell with no current IUT: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            this.setIUTInfo(rawValue, currentIUT);
                        }
                    }
                    case 4 -> { // new Departement (code)
                        if (currentIUT == null) {
                            LOG.warn("New departement cell with no current IUT: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            currentDept = new ExcelDepartement(rawValue);
                            currentIUT.getDepartements().add(currentDept);
                            currentDiplomeCode = null;
                        }
                    }
                    case 5 -> { // new Diploma (code)
                        if (currentDept == null) {
                            LOG.warn("New diplome cell with no current dept: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            currentDiplomeCode = rawValue;
                        }
                    }
                    case 6 -> { // parcours
                        if (currentDept == null) {
                            LOG.warn("New parcours cell with no current dept: " + cell.getAddress().formatAsR1C1String());
                        } else if (currentDiplomeCode == null) {
                            LOG.warn("New parcours cell with no current diplome code: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            currentDept.addParcours(currentDiplomeCode, rawValue);
                        }
                    }
                    default ->
                        LOG.debug("Outside scope cell of adress: " + cell.getAddress().formatAsR1C1String());
                }
                // Si on a atteind la 8ème colonne, on break la boucle de parcours des colonnes
                if (columnIdx >= 7) {
                    break;
                }
            }
        }
        // Consume last IUT
        if (currentIUT != null) {
            entityConsumer.accept(currentIUT);
        }
    }

    // (tel, adr, mel, contact, url, coordinates over 1 or 2 lines)
    private void setIUTInfo(String rawValue, ExcelIUT iut) {
        if (rawValue == null) {
            return;
        }
        // Test in order : Tel, GPS 2 coordinates, GPS 1 coordinate, mel, url, adr
        String val = TelFormater.matchesAndRetrieve(rawValue);
        if (val != null) {
            iut.setTel(val);
            return;
        }

        List<Double> gps2coors = GPSDoubleCoordinateFormater.matchesAndRetrieve(rawValue);
        if (gps2coors != null) {
            iut.setCoorGpsLat(gps2coors.get(0));
            iut.setCoorGpsLon(gps2coors.get(1));
            return;
        }

        Double gpsCoor = GPSCoordinateFormater.matchesAndRetrieve(rawValue);
        if (gpsCoor != null) {
            if (iut.getCoorGpsLat() == null) {
                iut.setCoorGpsLat(gpsCoor);
            } else {
                iut.setCoorGpsLon(gpsCoor);
            }
            return;
        }

        val = EmailFormater.matchesAndRetrieve(rawValue);
        if (val != null) {
            iut.setMel(val);
            return;
        }

        val = UrlFormater.matchesAndRetrieve(rawValue);
        if (val != null) {
            iut.setUrl(val);
            return;
        }

        val = BasicStringFormater.matchesAndRetrieve(rawValue);
        if (val != null) {
            // As contact may dwell, we only set adresse on first match
            if (iut.getAdresse() == null) {
                iut.setAdresse(val);
            }
            return;
        }
        LOG.warn("Invalid iut info value: " + rawValue);
    }

}
