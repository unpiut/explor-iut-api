/*
 * Copyright (C) 2024 IUT Laval - Le Mans Université.
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

import explorateurIUT.excelImport.model.ExcelAppText;
import explorateurIUT.model.AppText;
import java.util.function.Consumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 *
 * @author Rémi Venant
 */
public class MailTextConsumer implements Consumer<ExcelAppText> {

    private static final Log LOG = LogFactory.getLog(MailTextConsumer.class);
    private final static String DEFAULT_LANGUAGE = "fr";

    private final MongoTemplate mongoTemplate;

    public MailTextConsumer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void accept(ExcelAppText excelText) {
        //Create an instance of AppText then all instance of parcours
        LOG.debug("Save Mail Text " + excelText.getCode());
        mongoTemplate.save(textFromExcel(excelText));
    }

    private static AppText textFromExcel(ExcelAppText text) {
        return new AppText(text.getCode(), text.getContent(), DEFAULT_LANGUAGE, true);
    }
}
