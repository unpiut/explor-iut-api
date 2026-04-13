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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Rémi Venant
 */
@Configuration
public class MemoryModelConfiguration {

    private static final Log LOG = LogFactory.getLog(MemoryModelConfiguration.class);

    @PostConstruct
    public void init() {
        LOG.info("INIT " + this.getClass().getSimpleName());
    }

    @Bean
    public BUTIUTModelManager butIUTModelManager(Validator validator, ExcelDataFileLoader excelDataFileLoader, ExcelDataFileManagementService excelDataFileMgr) {
        LOG.info("Initiate but iut model manager...");
        final BUTIUTModelManagerImpl modelManager = new BUTIUTModelManagerImpl(validator);
        BUTIUTModel newModel = modelManager.startNewModelCreation();
        ConsumersHandler consHandler = new ConsumersHandler(newModel);
        try (InputStream excelInputStream = new FileInputStream(excelDataFileMgr.getCurrentFilePath().toFile())) {
            excelDataFileLoader.extractFromInputStream(consHandler.getExcelAppTextConsumer(),
                    consHandler.getExcelIUTConsumer(), consHandler.getExcelBUTConsumer(),
                    consHandler.getMailTextConsumer(), excelInputStream);
            newModel.commit();
        } catch (Exception ex) {
            LOG.error("Cannot initiate model manager with current excel data file: " + ex.getMessage());
        }
        return modelManager;
    }
}
