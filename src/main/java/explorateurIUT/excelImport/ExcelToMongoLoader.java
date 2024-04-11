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

import explorateurIUT.excelImport.consumers.BUTConsumer;
import explorateurIUT.excelImport.consumers.IUTConsumer;
import explorateurIUT.excelImport.model.ExcelBUT;
import explorateurIUT.excelImport.model.ExcelIUT;
import explorateurIUT.model.BUT;
import explorateurIUT.model.Departement;
import explorateurIUT.model.IUT;
import explorateurIUT.model.ParcoursBUT;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

/**
 *
 * @author Remi Venant
 */
public class ExcelToMongoLoader {

    private static final Log LOG = LogFactory.getLog(ExcelToMongoLoader.class);

    private final MongoTemplate mongoTemplate;

    private BUTConsumer butConsumer;
    private IUTConsumer iutconsumer;

    public ExcelToMongoLoader(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.butConsumer = new BUTConsumer(mongoTemplate);
        this.iutconsumer = new IUTConsumer(mongoTemplate, this.butConsumer.getParcoursButByCodeButParc());
    }

    public void reset() {
        this.butConsumer = new BUTConsumer(this.mongoTemplate);
        this.iutconsumer = new IUTConsumer(mongoTemplate, this.butConsumer.getParcoursButByCodeButParc());
    }

    public void clearDb() {
        final Query matchAll = new Query();
        Stream.of(Departement.class, IUT.class, ParcoursBUT.class, BUT.class)
                .forEach((c) -> this.mongoTemplate.remove(matchAll, c));
    }

    public Consumer<ExcelBUT> getExcelBUTConsumer() {
        return this.butConsumer;
    }

    public Consumer<ExcelIUT> getExcelIUTConsumer() {
        return this.iutconsumer;
    }
}
