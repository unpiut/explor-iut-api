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
package explorateurIUT.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;

/**
 *
 * @author Remi Venant
 */
public class BUTRepositoryImpl implements BUTRepositoryCustom {

    private static final Log LOG = LogFactory.getLog(BUTRepositoryImpl.class);

    private final MongoTemplate mongoTemplate;

    @Autowired
    public BUTRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<FiliereInfo> findMetiersByFiliere() {
        // Build a Map of BUT id - Filiere
        final Map<String, String> filiereByButId = this.findFiliereByButId();

        // Create en empty Map of Filiere - list of metier
        final ConcurrentHashMap<String, ConcurrentSkipListSet<String>> metiersByFilieres = new ConcurrentHashMap<>();

        // Stream PArcoursUBT(butId, metiers), and inject metiers in the map
        this.streamParcoursForMetiers().parallel().forEach((var metiersParcours) -> {
            String filiere = filiereByButId.get(metiersParcours.getBut());
            if (filiere == null) {
                LOG.warn("BUT not found for metiersParcours");
                return;
            }
            metiersByFilieres.compute(filiere, (var key, var metiers) -> {
                if (metiers == null) {
                    return new ConcurrentSkipListSet(metiersParcours.metiers);
                } else {
                    metiers.addAll(metiersParcours.metiers);
                    return metiers;
                }
            });
        });

        // Generate list of filiere info from the map
        return metiersByFilieres.entrySet().stream()
                .map((var entry) -> new FiliereInfo(entry.getKey(), new ArrayList<>(entry.getValue())))
                .toList();
    }

    private Map<String, String> findFiliereByButId() {
        Query q = new Query();
        q.fields().include("filiere");
        return this.mongoTemplate.stream(q, BUT.class)
                .collect(Collectors.toMap(BUT::getId, BUT::getFiliere));
    }

    private Stream<MetiersParcours> streamParcoursForMetiers() {
        Query q = new Query();
        q.fields().exclude("_id").include("but", "metiers");
        return this.mongoTemplate.stream(q, MetiersParcours.class, getCollectionNameFromDocument(ParcoursBUT.class));
    }

    private static class MetiersParcours {

        private String but;
        private List<String> metiers;

        public String getBut() {
            return but;
        }

        public void setBut(String but) {
            this.but = but;
        }

        public List<String> getMetiers() {
            return metiers;
        }

        public void setMetiers(List<String> metiers) {
            this.metiers = metiers;
        }

    }

    private static String getCollectionNameFromDocument(Class<?> documentClass) {
        Document docAnnotation = documentClass.getAnnotation(Document.class);
        if (docAnnotation == null) {
            throw new IllegalStateException("Give class is not a Mongo Document representation");
        }
        return docAnnotation.collection();
    }
}
