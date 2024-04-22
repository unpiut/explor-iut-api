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
package explorateurIUT;

import explorateurIUT.excelImport.AppDataProperties;
import explorateurIUT.excelImport.ExcelDataExtractor;
import explorateurIUT.excelImport.ExcelToMongoLoader;
import explorateurIUT.services.CacheManagementService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Profile("upload-data")
@Component
public class DataUploader implements CommandLineRunner{
    private static final Log LOG = LogFactory.getLog(DataLoader.class);

    private final AppDataProperties appDataProperties;

    private final MongoTemplate mongoTemplate;

    private final CacheManagementService cacheMgmtSvc;

    @Autowired
    public DataUploader(
            MongoTemplate mongoTemplate,
            CacheManagementService cacheMgmtSvc,
            AppDataProperties appDataProperties) {
        this.mongoTemplate = mongoTemplate;
        this.cacheMgmtSvc = cacheMgmtSvc;
        this.appDataProperties = appDataProperties;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        LOG.info("START DATA UPLOADING");

        LOG.info("Prepare mongo loader");
        ExcelToMongoLoader loader = new ExcelToMongoLoader(mongoTemplate);

        LOG.info("Clear database");
        loader.clearDb();

        LOG.info("Start ETL...");
        ExcelDataExtractor excelDataLoader = new ExcelDataExtractor(appDataProperties);
        excelDataLoader.extractInfoFromExcelFile(loader.getExcelIUTConsumer(), loader.getExcelBUTConsumer(), loader.getExcelAppTextConsumer());
        LOG.info("ETL end.");

        LOG.info("Clear cache");
        this.cacheMgmtSvc.resetCaches();
        final String etag = this.cacheMgmtSvc.setAndGetCacheEtag();
        LOG.info("Set new etag cache: " + etag);

        LOG.info("END DATA UPLOADING");
    }
}
