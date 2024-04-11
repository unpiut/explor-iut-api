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

import com.fasterxml.jackson.annotation.JsonIgnore;
import explorateurIUT.model.projections.DepartementSummary;
import explorateurIUT.model.projections.IUTSummary;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;

/**
 *
 * @author Remi Venant
 */
public class IUTRepositoryImpl implements IUTRepositoryCustom {

    private static final Log LOG = LogFactory.getLog(IUTRepositoryImpl.class);

    private final MongoTemplate mongoTemplate;

    @Autowired
    public IUTRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /*
    buts -> coll BUT (_id) puis Departement->IUT
    freeTextQuery -> coll  BUT
    latitude+longitude+radius -> coll IUT
    region -> coll IUT
    includeAllDepts -> si oui, reprendra tous les éléments des IUT
    
    Processus :
    1. requête BUT(_id, code) si buts ou freeTextQuery:text?  -> filterButs
    2. requête Deps(iut_id, _id) avec filterParcoursBUT:in? -> filteredDepts
    3: construire HashMap<iut_id, IUTSummary> de tous les iut_id de filteredDepts? avec regions? et GPSZone?
    4: si includeAllDepts: récup tous les DepartementSummary pour chaque iut_id sinon récup 
        tous DepartementSummary pour chaque dept de depts 
    5: rattacher deptSummaries aux IUTSummary
    
    returner collection de IUTSummary
     */
    @Override
    public Collection<IUTSummary> streamSummariesByFilter(IUTFormationFilter filter) {
        LOG.debug("Start filtering IUT on filter: ");
        // requête BUT(_id, code) si buts ou freeTextQuery:text?  -> filterButs
        List<ButInfo> filteredButs = null;
        if (filter.getButs() != null || filter.getFreeTextQuery() != null) {
            filteredButs = this.loadButs(filter.getButs(), filter.getFreeTextQuery()).toList();
            LOG.debug("filterButs computed: " + filteredButs.size());
            if (filteredButs.isEmpty()) { // if filtered but required but non match, stop here
                return Collections.EMPTY_LIST;
            }
        }
        // requête Deps(iut_id, _id) avec filterParcoursBUT:in? -> filteredDepts
        List<DeptInfo> filteredDepts = this.loadDepts(filteredButs).toList();
        LOG.debug("filterButs depts: " + filteredDepts.size());
        if (filteredDepts.isEmpty()) { // if filtered depts empty, stop here
            return Collections.EMPTY_LIST;
        }

        // construire HashMap<iut_id, IUTSummary> de tous les iut_id de filteredDepts? avec regions? et GPSZone?
        Set<ObjectId> filteredDeptIutIds = filteredDepts.stream().map(DeptInfo::getIut).collect(Collectors.toSet());
        Stream<IUTSummaryImpl> iutSummaryStream = this.loadIUTSummaries(filteredDeptIutIds,
                filter.getRegions(),
                filter.getLatitude(), filter.getLongitude(), filter.getRadiusKm());

        final Map<ObjectId, IUTSummaryImpl> iutSummaryById = iutSummaryStream.collect(Collectors.toMap((iutS) -> new ObjectId(iutS.getId()), Function.identity()));
        LOG.debug("iutSummaryById computed: " + iutSummaryById.size());

        // si includeAllDepts: récup tous les DepartementSummary pour chaque iut_id sinon récup tous DepartementSummary pour chaque dept de depts
        Stream<DepartementSummaryImpl> deptSummaryStream;
        if (filter.isIncludeAllDepts()) {
            deptSummaryStream = this.loadDeptSummaries(null, iutSummaryById.keySet());
        } else {
            deptSummaryStream = this.loadDeptSummaries(filteredDepts.stream().map(DeptInfo::getId).toList(), null);
        }

        // Ratacher DepartementSummary aux IUTSummary
        deptSummaryStream.forEach((var deptSummary) -> {
            IUTSummaryImpl iutSummary = iutSummaryById.get(deptSummary.getIut());
            // It is normal that iutSummary might not be found since a GPS filter may have skip it
            if (iutSummary != null) {
                iutSummary.addDepartementSummary(deptSummary);
            }
        });

        return iutSummaryById.values().stream().map(IUTSummary.class::cast).toList();
    }

    private Stream<ButInfo> loadButs(Collection<String> buts, String freeTextQuery) {
        Criteria c = new Criteria();
        if (buts != null) {
            c = c.and("code").in(buts);
        }
        Query query;
        if (freeTextQuery != null) {
            query = TextQuery
                    .queryText(new TextCriteria()
                            .caseSensitive(false)
                            .diacriticSensitive(false)
                            .matching(freeTextQuery))
                    .sortByScore()
                    .addCriteria(c);
        } else {
            query = Query.query(c);
        }
        query.fields().include("id", "code");
        return this.mongoTemplate.stream(query, ButInfo.class, getCollectionNameFromDocument(BUT.class));
    }

    private Stream<DeptInfo> loadDepts(Collection<ButInfo> fileteredButs) {
        Criteria c = new Criteria();
        if (fileteredButs != null) {
            c = c.and("butDispenses")
                    .elemMatch(Criteria.where("codeBut").in(fileteredButs.stream().map(ButInfo::getCode).toList()));
        }
        Query query = Query.query(c);
        query.fields().include("id", "iut");
        return this.mongoTemplate.stream(query, DeptInfo.class, getCollectionNameFromDocument(Departement.class));
    }

    private Stream<IUTSummaryImpl> loadIUTSummaries(Collection<ObjectId> iutIds, Collection<String> regions, Double latitude, Double longitude, Double radiusKm) {
        Criteria c = new Criteria();
        if (iutIds != null) {
            c = c.and("id").in(iutIds);
        }
        if (regions != null) {
            c = c.and("region").in(regions);
        }
        if (latitude != null) {
            c = c.and("location").withinSphere(new Circle(new Point(longitude, latitude),
                    new Distance(radiusKm, Metrics.KILOMETERS)));
        }
        Query q = Query.query(c);
        q.fields().include("nom", "site", "region", "location");
        return this.mongoTemplate.stream(q, IUTSummaryImpl.class, getCollectionNameFromDocument(IUT.class));
    }

    private Stream<DepartementSummaryImpl> loadDeptSummaries(Collection<ObjectId> deptIds, Collection<ObjectId> iutIds) {
        Criteria c = new Criteria();
        if (deptIds != null) {
            c = c.and("id").in(deptIds);
        }
        if (iutIds != null) {
            c = c.and("iut").in(iutIds);
        }
        Query q = Query.query(c);
        q.fields().include("code", "codesButDispenses", "iut");
        return this.mongoTemplate.stream(q, DepartementSummaryImpl.class, getCollectionNameFromDocument(Departement.class));
    }

    private static class ButInfo {

        private ObjectId id;
        private String code;

        public ButInfo() {
        }

        public ObjectId getId() {
            return id;
        }

        public void setId(ObjectId id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    private static class DeptInfo {

        private ObjectId id;
        private ObjectId iut;

        public DeptInfo() {
        }

        public ObjectId getId() {
            return id;
        }

        public void setId(ObjectId id) {
            this.id = id;
        }

        public ObjectId getIut() {
            return iut;
        }

        public void setIut(ObjectId iut) {
            this.iut = iut;
        }

    }

    private static class IUTSummaryImpl implements IUTSummary {

        private String id;
        private String nom;
        private String site;
        private String region;
        private GeoJsonPoint location;
        private final List<DepartementSummary> departements = new ArrayList<>();

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public String getNom() {
            return this.nom;
        }

        @Override
        public String getSite() {
            return this.site;
        }

        @Override
        public String getRegion() {
            return region;
        }

        @Override
        public GeoJsonPoint getLocation() {
            return this.location;
        }

        @Override
        public List<DepartementSummary> getDepartements() {
            return this.departements;
        }

        public boolean addDepartementSummary(DepartementSummary d) {
            return departements.add(d);
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public void setSite(String site) {
            this.site = site;
        }

        public void setLocation(GeoJsonPoint location) {
            this.location = location;
        }

        public void setRegion(String region) {
            this.region = region;
        }

    }

    private static class DepartementSummaryImpl implements DepartementSummary {

        private String id;
        private String code;
        private Set<ButAndParcoursDispenses> butDispenses;

        @JsonIgnore
        private ObjectId iut;

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public String getCode() {
            return this.code;
        }

        @Override
        public Set<ButAndParcoursDispenses> getButDispenses() {
            return this.butDispenses;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setButDispenses(Set<ButAndParcoursDispenses> butDispenses) {
            this.butDispenses = butDispenses;
        }

        public ObjectId getIut() {
            return iut;
        }

        public void setIut(ObjectId iut) {
            this.iut = iut;
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
