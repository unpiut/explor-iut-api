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

import explorateurIUT.DataLoader;
import explorateurIUT.excelImport.ExcelToMongoLoader;
import explorateurIUT.services.ExcelChangeService.ExcelChangeSession;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Julien Fourdan
 */
@Service
@Validated
public class DataUploadServiceImpl implements DataUploadService {

    private static final Log LOG = LogFactory.getLog(DataLoader.class);

    private final MongoTemplate mongoTemplate;

    private final CacheManagementService cacheMgmtSvc;

    private final ExcelDataExtractor excelDataExtractor;

    private final ExcelChangeService excelChangeSvc;

    @Autowired
    public DataUploadServiceImpl(MongoTemplate mongoTemplate, CacheManagementService cacheMgmtSvc,
            ExcelDataExtractor excelDataExtractor, ExcelChangeService excelChangeSvc) {
        this.mongoTemplate = mongoTemplate;
        this.cacheMgmtSvc = cacheMgmtSvc;
        this.excelDataExtractor = excelDataExtractor;
        this.excelChangeSvc = excelChangeSvc;
    }

    @Override
    @Transactional
    public void uploadData(MultipartFile dataExcelFile) throws IOException {
        LOG.info("START DATA UPLOADING");

        LOG.info("Prepare mongo loader");
        ExcelToMongoLoader loader = new ExcelToMongoLoader(mongoTemplate);
        
        LOG.info("Prepare File Change Session");
        ExcelChangeSession changeSession = this.excelChangeSvc.getChangeExcelSession();
        
        try {
            LOG.info("Clear database");
            loader.clearDb();

            LOG.info("Re-populate database");
            try (InputStream dataExcel = dataExcelFile.getInputStream()) {
                excelDataExtractor.extractFromInputStream(loader.getExcelAppTextConsumer(), loader.getExcelIUTConsumer(), loader.getExcelBUTConsumer(), dataExcel);
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
            LOG.info("END DATA UPLOADING");
        } catch (Throwable ex) {
            changeSession.rollback();
            throw ex;
        }
    }
}