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

import explorateurIUT.excelImport.model.ExcelBUT;
import explorateurIUT.excelImport.model.ExcelParcoursBUT;
import explorateurIUT.model.BUT;
import explorateurIUT.model.ParcoursBUT;
import java.util.HashMap;
import java.util.function.Consumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 *
 * @author Remi Venant
 */
public class BUTConsumer implements Consumer<ExcelBUT> {
    
    private static final Log LOG = LogFactory.getLog(BUTConsumer.class);

    private final MongoTemplate mongoTemplate;

    private final HashMap<String, BUT> butByCode = new HashMap<>();
    private final HashMap<String, ParcoursBUT> parcoursButByCodeButParc = new HashMap<>();

    public BUTConsumer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public HashMap<String, BUT> getButByCode() {
        return butByCode;
    }

    public HashMap<String, ParcoursBUT> getParcoursButByCodeButParc() {
        return parcoursButByCodeButParc;
    }

    @Override
    public void accept(ExcelBUT excelBut) {
        //Create an instance of BUT then all instance of parcours
        LOG.debug("Save BUT " + excelBut.getCode());
        final BUT but = mongoTemplate.save(butFromExcel(excelBut));
        butByCode.put(but.getCode(), but);
        excelBut.getParcours().forEach((excelParcours) -> {
            LOG.debug("- Save Parcours " + excelParcours.getCode());
            ParcoursBUT parcours = mongoTemplate.save(parcoursFromExcel(but, excelParcours));
            String key = but.getCode() + "_" + parcours.getCode();
            parcoursButByCodeButParc.put(key, parcours);
        });
    }

    private static BUT butFromExcel(ExcelBUT but) {
        return new BUT(but.getCode(), but.getNom(), but.getFiliere(), but.getMetiers(), but.getDescription(), but.getUrlFiche(), but.getUrlFranceCompetence(), but.getUniversMetiers());
    }

    private static ParcoursBUT parcoursFromExcel(BUT but, ExcelParcoursBUT parcours) {
        return new ParcoursBUT(but, parcours.getCode(), parcours.getNom());
    }

}
