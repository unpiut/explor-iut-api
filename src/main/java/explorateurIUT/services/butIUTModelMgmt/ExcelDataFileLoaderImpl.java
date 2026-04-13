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
package explorateurIUT.services.butIUTModelMgmt;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.UnsupportedFileFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import explorateurIUT.services.butIUTModelMgmt.excelImport.extractors.AppTextExtractor;
import explorateurIUT.services.butIUTModelMgmt.excelImport.extractors.BUTExtractor;
import explorateurIUT.services.butIUTModelMgmt.excelImport.extractors.IUTExtractor;
import explorateurIUT.services.butIUTModelMgmt.excelImport.model.ExcelAppText;
import explorateurIUT.services.butIUTModelMgmt.excelImport.model.ExcelBUT;
import explorateurIUT.services.butIUTModelMgmt.excelImport.model.ExcelIUT;
import java.util.function.Consumer;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 *
 * @author Rémi Venant
 */
@Service
@Validated
public class ExcelDataFileLoaderImpl implements ExcelDataFileLoader {

    private static final Log LOG = LogFactory.getLog(ExcelDataFileLoaderImpl.class);

    private final AppDataProperties appDataProperties;

    @Autowired
    public ExcelDataFileLoaderImpl(AppDataProperties appDataProperties) {
        this.appDataProperties = appDataProperties;
    }

    @Override
    public void extractFromInputStream(Consumer<ExcelAppText> appTextConsumer, Consumer<ExcelIUT> iutConsumer, Consumer<ExcelBUT> butConsumer, Consumer<ExcelAppText> mailTextConsumer, InputStream inputStream) throws IOException {
        LOG.info("ETL Excel data: start...");
        try (final XSSFWorkbook wb = new XSSFWorkbook(inputStream)) {
            // Process BUT
            LOG.debug("Process sheet " + this.appDataProperties.getButSheetName());
            final BUTExtractor butExtractor = new BUTExtractor();
            butExtractor.extractEntities(wb.getSheet(this.appDataProperties.getButSheetName()), butConsumer);
            // Process DUT
            LOG.debug("Process sheet " + this.appDataProperties.getIutSheetName());
            final IUTExtractor iutExtractor = new IUTExtractor();
            iutExtractor.extractEntities(wb.getSheet(this.appDataProperties.getIutSheetName()), iutConsumer);
            // Process App texts
            LOG.debug("Process sheet " + this.appDataProperties.getAppTextSheetName());
            final AppTextExtractor appTextExtractor = new AppTextExtractor();
            appTextExtractor.extractEntities(wb.getSheet(this.appDataProperties.getAppTextSheetName()), appTextConsumer);
            // Process Mail Texts
            LOG.debug("Process sheet " + this.appDataProperties.getMailTextSheetName());
            appTextExtractor.extractEntities(wb.getSheet(this.appDataProperties.getMailTextSheetName()), mailTextConsumer);
        } catch (IOException | UnsupportedFileFormatException ex) {
            LOG.warn(String.format("Cannot complete etl", ex.getMessage()));
            throw ex;
        }
        LOG.info("ETL data: done.");
    }
}
