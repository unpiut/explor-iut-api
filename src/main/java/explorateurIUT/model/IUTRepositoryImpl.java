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
import org.springframework.data.mongodb.core.schema.JsonSchemaObject;

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
    x freeTextQuery -> coll  ParcoursBUT
    latitude+longitude+radius -> coll IUT
    x buts -> coll BUT (_id) puis Departement->IUT
    x blockButs -> coll BUT (_id) puis ParcoursBUT (_id) puis coll Parcoursdepts->Departement->IUT
    x jobs -> collec ParcoursBUT (_id) puis ParcoursDep->Departement->IUT
    includeAllDepts -> si oui, reprendra tous les éléments des IUT
    
    Processus :
    1. requête BUT(_id) si buts -> filterButObjectIds
    2. requête ParcoursBUT(_id) avec filterButObjectIds:in? && freeTextQuery:text?  && jobs:in? -> filterParcoursBUT
    3. requête ParcoursDept(iut_id, departement_id) avec filterParcoursBUT:in? && block -> parcoursDepts
    4. si parcoursDepts:
        construire HashMap<iut_id, IUTSummary> de tous les iut_id de parcoursDepts avec GPSZone?
        si includeAllDepts: récup tous DepartementSummary pour chaque iut_id et les ratacher au IUTSummary
        sinon récup tous DepartementSummary pour chaque departement_id de parcoursDepts et les ratacher au IUTSummary
    sinon:
        construire HashMap<iut_id, IUTSummary> avec GPSZone?
        récup tous DepartementSummary de chaque iut de la map et les ratacher au IUTSummary
    
    return  HashMap<iut_id, IUTSummary>.values()
     */
    @Override
    public Collection<IUTSummary> streamSummariesByFilter(IUTFormationFilter filter) {
        LOG.debug("Start filtering IUT on filter: ");
        // requête BUT(_id) si buts -> filterButObjectIds
        List<ObjectId> filterButIds = null;
        if (filter.getButs() != null) {
            filterButIds = this.loadButIds(filter.getButs()).toList();
            LOG.debug("filterButIds computed: " + filterButIds);
        }
        // requête ParcoursBUT(_id) avec filterButObjectIds:in? && freeTextQuery:text?  && jobs:in? -> filterParcoursBUTids
        List<ObjectId> filterParcoursBUTids = null;
        if (filterButIds != null || filter.getFreeTextQuery() != null || filter.getJobs() != null) {
            // On limite au 20 premier résultats
            filterParcoursBUTids = this.loadParcoursButIds(filterButIds, filter.getFreeTextQuery(), filter.getJobs()).toList();
            LOG.debug("filterParcoursBUTids computed: " + filterParcoursBUTids);
        }
        // requête ParcoursDept(iut_id, departement_id) avec filterParcoursBUT:in? && blockBut?alternance:notEmpty &&blockOnly -> parcoursDepts
        List<ParcoursDeptIds> parcoursDeptIds = null;
        if (filterParcoursBUTids != null) {
            parcoursDeptIds = this.loadParcoursDeptIds(filterParcoursBUTids, filter.isBlockOnly()).toList();
            LOG.debug("parcoursDeptIds computed: " + parcoursDeptIds);
        }

        // construction HashMap<iut_id, IUTSummary> selon présence parcoursDepts
        Stream<IUTSummaryImpl> iutSummaryStream;
        if (parcoursDeptIds != null) {
            iutSummaryStream = this.loadIUTSummaries(
                    parcoursDeptIds.stream().map(ParcoursDeptIds::getIut).collect(Collectors.toSet()),
                    filter.getLatitude(), filter.getLongitude(), filter.getRadiusKm());
        } else {
            iutSummaryStream = this.loadIUTSummaries(null, filter.getLatitude(), filter.getLongitude(), filter.getRadiusKm());
        }
        final Map<ObjectId, IUTSummaryImpl> iutSummaryById = iutSummaryStream.collect(Collectors.toMap((iutS) -> new ObjectId(iutS.getId()), Function.identity()));
        LOG.debug("iutSummaryById computed: " + iutSummaryById);

        // Récupération liste DepartementSummary selon presence parcoursDepts et indicateur includeAllDepts
        Stream<DepartementSummaryImpl> deptSummaryStream;
        if (parcoursDeptIds != null && !filter.isIncludeAllDepts()) {
            deptSummaryStream = this.loadDeptSummaries(parcoursDeptIds.stream().map(ParcoursDeptIds::getDepartement).toList(), null);
        } else {
            deptSummaryStream = this.loadDeptSummaries(null, iutSummaryById.keySet());
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

    private Stream<ObjectId> loadButIds(Collection<String> buts) {
        Criteria c = Criteria.where("code").in(buts);
        Query q = Query.query(c);
        q.fields().include("id");
        return this.mongoTemplate.stream(q, BUT.class).map(BUT::getId).map(ObjectId::new);
    }

    private Stream<ObjectId> loadParcoursButIds(Collection<ObjectId> butIds, String freeTextQuery, Collection<String> jobs) {
        Criteria c = new Criteria();
        if (butIds != null) {
            c = c.and("but").in(butIds);
        }
        if (jobs != null) {
            c = c.and("metiers").in(jobs);
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
        query.fields().include("id");
        return this.mongoTemplate.stream(query, ParcoursBUT.class).map(ParcoursBUT::getId).map(ObjectId::new);
    }

    private Stream<ParcoursDeptIds> loadParcoursDeptIds(Collection<ObjectId> parcoursButIds, boolean blockOnly) {
        Criteria c = new Criteria();
        if (parcoursButIds != null) {
            c = c.and("parcoursBUT").in(parcoursButIds);
        }
        if (blockOnly) {
            c = c.and("alternances").exists(true).type(JsonSchemaObject.Type.ARRAY).ne(List.of());
        }
        Query q = Query.query(c);
        q.fields().include("iut", "departement");
        return this.mongoTemplate.stream(q, ParcoursDeptIds.class, getCollectionNameFromDocument(ParcoursDept.class));
    }

    private Stream<IUTSummaryImpl> loadIUTSummaries(Collection<ObjectId> iutIds, Double latitude, Double longitude, Double radiusKm) {
        Criteria c = new Criteria();
        if (iutIds != null) {
            c = c.and("id").in(iutIds);
        }
        if (latitude != null) {
            c = c.and("location").withinSphere(new Circle(new Point(latitude, longitude),
                    new Distance(radiusKm, Metrics.KILOMETERS)));
        }
        Query q = Query.query(c);
        q.fields().include("nom", "site", "location");
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

    private static class ParcoursDeptIds {

        private ObjectId iut;
        private ObjectId departement;

        public ObjectId getIut() {
            return iut;
        }

        public void setIut(ObjectId iut) {
            this.iut = iut;
        }

        public ObjectId getDepartement() {
            return departement;
        }

        public void setDepartement(ObjectId departement) {
            this.departement = departement;
        }

    }

    private static class IUTSummaryImpl implements IUTSummary {

        private String id;
        private String nom;
        private String site;
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
    }

    private static class DepartementSummaryImpl implements DepartementSummary {

        private String id;
        private String code;
        private Set<String> codesButDispenses;

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
        public Set<String> getCodesButDispenses() {
            return this.codesButDispenses;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setCodesButDispenses(Set<String> codesButDispenses) {
            this.codesButDispenses = codesButDispenses;
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
