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

import explorateurIUT.excelImport.extractors.BUTExtractor;
import explorateurIUT.excelImport.extractors.IUTExtractor;
import explorateurIUT.excelImport.model.ExcelBUT;
import explorateurIUT.excelImport.model.ExcelIUT;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.function.Consumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.UnsupportedFileFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Remi Venant
 */
public class ExcelDataExtractor {

    private static final Log LOG = LogFactory.getLog(ExcelDataExtractor.class);

    private final AppDataProperties appDataProperties;

    public ExcelDataExtractor(AppDataProperties appDataProperties) {
        this.appDataProperties = appDataProperties;
    }

    public void extractInfoFromExcelFile(Consumer<ExcelIUT> iutInfoConsummer, Consumer<ExcelBUT> butInfoConsummer) {
        LOG.info(String.format("Extraction from file %s...", this.appDataProperties.getFilePath()));
        try (FileInputStream fis = new FileInputStream(new File(this.appDataProperties.getFilePath()))) {
            final XSSFWorkbook wb = new XSSFWorkbook(fis);
            // First we process BUT
            LOG.info("Extract and Load BUT...");
            final BUTExtractor butExtractor = new BUTExtractor();
            butExtractor.extractEntities(wb.getSheet(this.appDataProperties.getButSheetName()), butInfoConsummer);
            // Then we process DUT
            LOG.info("Extract and Load IUT...");
            final IUTExtractor iutExtractor = new IUTExtractor();
            iutExtractor.extractEntities(wb.getSheet(this.appDataProperties.getIutSheetName()), iutInfoConsummer);
        } catch (IOException | UnsupportedFileFormatException ex) {
            LOG.warn(String.format("Cannot load score file \"%s\": %s", this.appDataProperties.getFilePath(), ex.getMessage()));
        }
        LOG.info("ETL done.");
    }

}
