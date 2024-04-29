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
package explorateurIUT.excelImport.consumers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import java.util.function.Consumer;
import explorateurIUT.excelImport.model.ExcelAppText;
import explorateurIUT.model.AppText;

/**
 *
 * @author Julien Fourdan
 */
public class AppTextConsumer implements Consumer<ExcelAppText> {

    private static final Log LOG = LogFactory.getLog(BUTConsumer.class);
    private final static String DEFAULT_LANGUAGE = "fr";

    private final MongoTemplate mongoTemplate;

    public AppTextConsumer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void accept(ExcelAppText excelText) {
        //Create an instance of AppText then all instance of parcours
        LOG.debug("Save App Text " + excelText.getCode());
        mongoTemplate.save(textFromExcel(excelText));
    }

    private static AppText textFromExcel(ExcelAppText text) {
        return new AppText(text.getCode(), text.getContent(), DEFAULT_LANGUAGE);
    }
}
