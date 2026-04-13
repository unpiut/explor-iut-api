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
package explorateurIUT.services;

import explorateurIUT.services.butIUTModelMgmt.ExcelDataFileManagementService;
import explorateurIUT.CacheCleaner;
import explorateurIUT.services.butIUTModelMgmt.BUTIUTModel;
import explorateurIUT.services.butIUTModelMgmt.BUTIUTModelManager;
import explorateurIUT.services.butIUTModelMgmt.ExcelDataFileLoader;
import explorateurIUT.services.butIUTModelMgmt.ExcelDataFileManagementService.ExcelChangeSession;
import explorateurIUT.services.butIUTModelMgmt.excelToMemoryConsumers.ConsumersHandler;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Julien Fourdan
 */
@Service
@Validated
public class DataUploadServiceImpl implements DataUploadService {

    private static final Log LOG = LogFactory.getLog(CacheCleaner.class);

    private final BUTIUTModelManager modelManager;

    private final CacheManagementService cacheMgmtSvc;

    private final ExcelDataFileLoader excelDataFileLoader;

    private final ExcelDataFileManagementService excelDateFileMgr;

    @Autowired
    public DataUploadServiceImpl(BUTIUTModelManager modelManager, CacheManagementService cacheMgmtSvc,
            ExcelDataFileLoader excelDataFileLoader, ExcelDataFileManagementService excelDataFileMgr) {
        this.modelManager = modelManager;
        this.cacheMgmtSvc = cacheMgmtSvc;
        this.excelDataFileLoader = excelDataFileLoader;
        this.excelDateFileMgr = excelDataFileMgr;
    }

    @Override
    public void uploadData(MultipartFile dataExcelFile) throws IOException {
        LOG.info("START DATA UPLOADING");

        LOG.info("Prepare new model and consumers");
        BUTIUTModel newModel = this.modelManager.startNewModelCreation();
        ConsumersHandler consHandler = new ConsumersHandler(newModel);

        LOG.info("Prepare File Change Session");
        ExcelChangeSession changeSession = this.excelDateFileMgr.getChangeExcelSession();

        try {
            LOG.info("Forge model");
            try (InputStream excelInputStream = dataExcelFile.getInputStream()) {
                excelDataFileLoader.extractFromInputStream(consHandler.getExcelAppTextConsumer(),
                        consHandler.getExcelIUTConsumer(), consHandler.getExcelBUTConsumer(),
                        consHandler.getMailTextConsumer(), excelInputStream);
            }

            LOG.info("Change excel");
            try (InputStream dataExcel = dataExcelFile.getInputStream()) {
                changeSession.applyChange(dataExcel);
            }

            //TODO : replace the existing excel file with the one given in parameter.
            LOG.info("Clear cache");
            this.cacheMgmtSvc.resetCaches();
            final String etag = this.cacheMgmtSvc.setAndGetCacheEtag();
            LOG.info("Set new etag cache: " + etag);

            // Commit file change
            changeSession.commit();
            // Commit model
            newModel.commit();
            LOG.info("END DATA UPLOADING");
        } catch (Throwable ex) {
            changeSession.rollback();
            newModel.rollback();
            throw ex;
        }
    }
}
