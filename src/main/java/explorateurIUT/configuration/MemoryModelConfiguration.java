/*
 * Copyright (C) 2026 IUT Laval - Le Mans Université.
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
package explorateurIUT.configuration;

import explorateurIUT.services.butIUTModelMgmt.BUTIUTModel;
import explorateurIUT.services.butIUTModelMgmt.BUTIUTModelManager;
import explorateurIUT.services.butIUTModelMgmt.BUTIUTModelManagerImpl;
import explorateurIUT.services.butIUTModelMgmt.ExcelDataFileLoader;
import explorateurIUT.services.butIUTModelMgmt.ExcelDataFileManagementService;
import explorateurIUT.services.butIUTModelMgmt.excelToMemoryConsumers.ConsumersHandler;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Validator;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Rémi Venant
 */
@Configuration
public class MemoryModelConfiguration {

    private static final Log LOG = LogFactory.getLog(MemoryModelConfiguration.class);

    @Autowired
    private ExcelDataFileLoader excelDataFileLoader;

    @Autowired
    private ExcelDataFileManagementService excelDataFileMgr;

    @PostConstruct
    public void init() {
        LOG.info("INIT Memory Model Configuration");
    }

    //@ConditionalOnMissingBean // since overidden in some unit tests
    @Bean
    public BUTIUTModelManager butIUTModelManager(Validator validator) {
        LOG.info("Initiate but iut model manager...");
        final BUTIUTModelManagerImpl modelManager = new BUTIUTModelManagerImpl(validator, this::initModelManager);
        return modelManager;
    }

    protected void initModelManager(BUTIUTModel newModel) {
        ConsumersHandler consHandler = new ConsumersHandler(newModel);

        LOG.info("Load initial data...");
        if (excelDataFileMgr.hasKnownCurrentFilePath()) {
            LOG.info("Load initial data...");
            try (InputStream excelInputStream = new FileInputStream(excelDataFileMgr.getCurrentFilePath().toFile())) {
                excelDataFileLoader.extractFromInputStream(consHandler.getExcelAppTextConsumer(),
                        consHandler.getExcelIUTConsumer(), consHandler.getExcelBUTConsumer(),
                        consHandler.getMailTextConsumer(), excelInputStream);
            } catch (Exception ex) {
                LOG.error("Cannot initiate model manager with current excel data file: " + ex.getMessage());
                throw new IllegalStateException("Error while saving model from excel file: " + ex.getMessage(), ex);
            }
        } else {
            LOG.error("No current data file to fill the BUT IUT model !");
        }
    }
}
