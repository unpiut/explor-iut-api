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

import explorateurIUT.excelImport.formatters.BasicStringFormater;
import explorateurIUT.excelImport.formatters.EmailFormater;
import explorateurIUT.excelImport.formatters.GPSCoordinateFormater;
import explorateurIUT.excelImport.formatters.GPSDoubleCoordinateFormater;
import explorateurIUT.excelImport.formatters.TelFormater;
import explorateurIUT.excelImport.formatters.UrlFormater;
import explorateurIUT.excelImport.model.ExcelAnneeAlt;
import explorateurIUT.excelImport.model.ExcelBUT;
import explorateurIUT.excelImport.model.ExcelDepartement;
import explorateurIUT.excelImport.model.ExcelDiplomeDept;
import explorateurIUT.excelImport.model.ExcelIUT;
import explorateurIUT.excelImport.model.ExcelParcoursBUT;
import explorateurIUT.excelImport.model.ExcelParcoursDeptDip;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.UnsupportedFileFormatException;
import org.apache.poi.ss.usermodel.Cell;
import static org.apache.poi.ss.usermodel.CellType.BLANK;
import static org.apache.poi.ss.usermodel.CellType.BOOLEAN;
import static org.apache.poi.ss.usermodel.CellType.ERROR;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;
import static org.apache.poi.ss.usermodel.CellType._NONE;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Remi Venant
 */
public class ExcelDataExtractor {

    private static final Log LOG = LogFactory.getLog(ExcelDataExtractor.class);

    private String iutInfoPageName = "IUT";
    private String butInfoPageName = "BUT";
    private final String excelFilePath;

    public ExcelDataExtractor(String excelFilePath) {
        this.excelFilePath = excelFilePath;
    }

    public void extractInfoFromExcelFile(Consumer<ExcelIUT> iutInfoConsummer, Consumer<ExcelBUT> butInfoConsummer) {
        LOG.info(String.format("Loading info file %s...", excelFilePath));
        try (FileInputStream fis = new FileInputStream(new File(excelFilePath))) {
            final XSSFWorkbook wb = new XSSFWorkbook(fis);
            // First we process BUT
            this.extractBUTInfo(wb.getSheet(this.butInfoPageName), butInfoConsummer);
            // Then we process DUT
            this.extractIUTInfo(wb.getSheet(this.iutInfoPageName), iutInfoConsummer);

        } catch (IOException | UnsupportedFileFormatException ex) {
            LOG.warn(String.format("Cannot load score file \"%s\": %s", excelFilePath, ex.getMessage()));
        }
    }

    private void extractIUTInfo(XSSFSheet sheet, Consumer<ExcelIUT> iutInfoConsummer) {
        LOG.debug("Loading excel sheet " + sheet.getSheetName());
        //Iterate over rows
        final Iterator<Row> itRows = sheet.rowIterator();
        //Skip first row (headings)
        itRows.next();

        //Iterate over remaining lines to complete IUT Info
        ExcelIUT currentIUT = null;
        ExcelDepartement currentDept = null;
        ExcelDiplomeDept currentDiplome = null;
        ExcelParcoursDeptDip currentParcours = null;
        ExcelAnneeAlt currentAnneeAlt = null;

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
                            iutInfoConsummer.accept(currentIUT);
                            currentIUT = null;
                        }
                        LOG.debug("Create new IUT of name " + rawValue);
                        currentIUT = new ExcelIUT(rawValue);
                    }
                    case 1 -> {// IUT Town (should create a new IUT if city already defined
                        if (currentIUT == null) {
                            LOG.warn("IUT town cell with no current IUT: " + cell.getAddress().formatAsR1C1String());
                        } else if (currentIUT.getVille() == null) {
                            currentIUT.setVille(rawValue);
                        } else {
                            String iutName = currentIUT.getNom();
                            iutInfoConsummer.accept(currentIUT);
                            LOG.debug("Create new IUT of name with new city " + iutName);
                            currentIUT = new ExcelIUT(iutName);
                            currentIUT.setVille(rawValue);
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
                            currentDiplome = null;
                            currentParcours = null;
                            currentAnneeAlt = null;
                        }
                    }
                    case 5 -> { // Departmeent Info (mel, tel, contact, url)
                        if (currentDept == null) {
                            LOG.warn("IUT info cell with no current IUT: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            this.setDeptInfo(rawValue, currentDept);
                        }
                    }
                    case 6 -> { // new Diploma (code)
                        if (currentDept == null) {
                            LOG.warn("New diplome cell with no current dept: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            currentDiplome = new ExcelDiplomeDept(rawValue);
                            currentDept.getDiplomes().add(currentDiplome);
                            currentParcours = null;
                            currentAnneeAlt = null;
                        }
                    }
                    case 7 -> { // parcours
                        if (currentDiplome == null) {
                            LOG.warn("New parcours cell with no current diplome: " + cell.getAddress().formatAsR1C1String() + " parc: " + rawValue);
                        } else {
                            currentParcours = new ExcelParcoursDeptDip(rawValue);
                            currentDiplome.getParcours().add(currentParcours);
                            // currentAnneeAlt = null; On n'efface pas une année courant en changement de parcours pour accepter les représentations comprimées
                        }
                    }
                    case 8 -> { // new year alt
                        if (currentParcours == null) {
                            LOG.warn("New year alt cell with no current parcours: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            Integer year = extractIntegerValue(cell);
                            if (year == null) {
                                LOG.warn("Bad year code cell: " + cell.getAddress().formatAsR1C1String() + " : " + cell.toString());
                            } else {
                                currentAnneeAlt = new ExcelAnneeAlt(year);
                                currentParcours.getAnneesAlt().add(currentAnneeAlt);
                            }
                        }
                    }
                    case 9 -> { // year alt info (mel, tel, contact, urlCal):
                        if (currentAnneeAlt == null) {
                            LOG.warn("IUT annee alt info cell with no current anne alt: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            this.setAnneeAltInfo(rawValue, currentAnneeAlt);
                        }
                    }
                    default ->
                        LOG.debug("Outside scope cell of adress: " + cell.getAddress().formatAsR1C1String());
                }
                // Si on a atteind la 8ème colonne, on break la boucle de parcours des colonnes
                if (columnIdx >= 9) {
                    break;
                }
            }
        }
        // Consume last IUT
        if (currentIUT != null) {
            iutInfoConsummer.accept(currentIUT);
        }
    }

    // (tel, adr, mel, contact, url, coordinates over 1 or 2 lines)
    private void setIUTInfo(String rawValue, ExcelIUT iut) {
        if (rawValue == null) {
            return;
        }
        // Test in order : Tel, GPS 2 coordinates, GPS 1 coordinate, mel, url, adr, contact
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
            if (iut.getAdresse() == null) {
                iut.setAdresse(val);
            } else {
                iut.setContact(val);
            }
            return;
        }
        LOG.warn("Invalid iut info value: " + rawValue);
    }

    // Departmeent Info (mel, tel, contact, url)
    private void setDeptInfo(String rawValue, ExcelDepartement dept) {
        if (rawValue == null) {
            return;
        }
        // Test in order : Tel, mel, url, contact
        String val = TelFormater.matchesAndRetrieve(rawValue);
        if (val != null) {
            dept.setTel(val);
            return;
        }
        val = EmailFormater.matchesAndRetrieve(rawValue);
        if (val != null) {
            dept.setMel(val);
            return;
        }
        val = UrlFormater.matchesAndRetrieve(rawValue);
        if (val != null) {
            dept.setUrl(val);
            return;
        }
        val = BasicStringFormater.matchesAndRetrieve(rawValue);
        if (val != null) {
            dept.setContact(val);
            return;
        }
        LOG.warn("Invalid dept info value: " + rawValue);
    }

    // year alt info (mel, tel, contact, urlCal):
    private void setAnneeAltInfo(String rawValue, ExcelAnneeAlt anneeAlt) {
        if (rawValue == null) {
            return;
        }
        // Test in order : Tel, mel, urlCal, contact
        String val = TelFormater.matchesAndRetrieve(rawValue);
        if (val != null) {
            anneeAlt.setTel(val);
            return;
        }
        val = EmailFormater.matchesAndRetrieve(rawValue);
        if (val != null) {
            anneeAlt.setMel(val);
            return;
        }
        val = UrlFormater.matchesAndRetrieve(rawValue);
        if (val != null) {
            anneeAlt.setUrlCal(val);
            return;
        }
        val = BasicStringFormater.matchesAndRetrieve(rawValue);
        if (val != null) {
            anneeAlt.setContact(val);
            return;
        }
        LOG.warn("Invalid annee alt info value: " + rawValue);
    }

    private void extractBUTInfo(XSSFSheet sheet, Consumer<ExcelBUT> butInfoConsummer) {
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
                String rawValue = extractCellValue(cell);
                if (rawValue == null || rawValue.isBlank()) {
                    continue;
                }
                rawValue = rawValue.trim();
                switch (cell.getAddress().getColumn()) {
                    case 0 -> { // BUT Code, new BUT
                        if (currentBut != null) {
                            butInfoConsummer.accept(currentBut);
                            currentBut = null;
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
                    case 5 -> { // Parcours mot-clé
                        if (currentParcours == null) {
                            LOG.warn("New Parcours mot-cle cell with no current parcours: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            currentParcours.setMotsCles(rawValue.replaceAll("\\s*,\\s*", " "));
                        }
                    }
                    case 6 -> { // Parcours métiers
                        if (currentParcours == null) {
                            LOG.warn("New Parcours metiers cell with no current parcours: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            currentParcours.setMetiers(Arrays.asList(rawValue.split("\\s*,\\s*")));
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
            }
        }

        if (currentBut != null) {
            butInfoConsummer.accept(currentBut);
        }
    }

    private static String extractCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING -> {
                return cell.getStringCellValue();
            }
            case NUMERIC -> {
                return Double.toString(cell.getNumericCellValue());
            }
            case BOOLEAN -> {
                return Boolean.toString(cell.getBooleanCellValue());
            }
            case BLANK -> {
                return null;
            }
            case _NONE -> {
                return null;
            }
            case ERROR -> {
                return cell.getCellFormula();
            }
            default -> {
                LOG.warn("Cell not valid! CellType: " + cell.getCellType());
                return null;
            }
        }
    }

    private static Integer extractIntegerValue(Cell cell) {
        switch (cell.getCellType()) {
            case NUMERIC -> {
                return (int) cell.getNumericCellValue();
            }
            case BLANK -> {
                return null;
            }
            case _NONE -> {
                return null;
            }
            default -> {
                LOG.warn("Cell not valid for numeric value! CellType: " + cell.getCellType());
                return null;
            }
        }
    }

    private static boolean isComment(String value) {
        return value.startsWith("#") || value.startsWith("/");
    }
}
