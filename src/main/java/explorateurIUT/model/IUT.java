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

import com.fasterxml.jackson.annotation.JsonView;
import explorateurIUT.model.views.IUTViews;
import jakarta.validation.constraints.Email;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 *
 * @author Remi Venant
 */
@Document(collection = "IUT", language = "french")
public class IUT {

    @JsonView(IUTViews.Normal.class)
    @Id
    private String id;

    @JsonView(IUTViews.Normal.class)
    private String nom;

    @JsonView(IUTViews.Normal.class)
    private String site;

    @JsonView(IUTViews.Normal.class)
    private String region;

    @JsonView(IUTViews.Details.class)
    private String address;

    @JsonView(IUTViews.Details.class)
    private String tel;

    @JsonView(IUTViews.Details.class)
    @Email
    private String mel;

    @JsonView(IUTViews.Details.class)
    private String urlWeb;

    @JsonView(IUTViews.Normal.class)
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    @JsonView(IUTViews.Normal.class)
    @ReadOnlyProperty
    @DocumentReference(lookup = "{'iut':?#{#self._id} }", lazy = true)
    private List<Departement> departements;

    protected IUT() {
    }

    public IUT(String nom) {
        this.nom = nom;
    }

    public IUT(String nom, String site, String region, String address, String tel, String mel, String urlWeb, GeoJsonPoint location) {
        this.nom = nom;
        this.site = site;
        this.region = region;
        this.address = address;
        this.tel = tel;
        this.mel = mel;
        this.urlWeb = urlWeb;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMel() {
        return mel;
    }

    public void setMel(String mel) {
        this.mel = mel;
    }

    public String getUrlWeb() {
        return urlWeb;
    }

    public void setUrlWeb(String urlWeb) {
        this.urlWeb = urlWeb;
    }

    public GeoJsonPoint getLocation() {
        return location;
    }

    public void setLocation(GeoJsonPoint location) {
        this.location = location;
    }

    public List<Departement> getDepartements() {
        return departements == null ? List.of() : Collections.unmodifiableList(this.departements);
    }

    protected void setDepartements(List<Departement> departements) {
        this.departements = departements;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IUT other = (IUT) obj;
        return Objects.equals(this.id, other.id);
    }

}
