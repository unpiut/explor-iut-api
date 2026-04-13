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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Remi Venant
 */
public class IUT {

    @JsonView(IUTViews.Normal.class)
    private String id; // will use a fixed genereated id

    @JsonView(IUTViews.Normal.class)
    @NotBlank
    private String nom;

    @JsonView(IUTViews.Normal.class)
    @NotBlank
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
    @NotNull
    private GeoJsonPoint location;

    @JsonView(IUTViews.Normal.class)
    private List<Departement> departements;

    protected IUT() {
    }

    public IUT(String nom) {
        this.nom = nom;
    }

    public IUT(String nom, String site, String region, String address, String tel, String mel, String urlWeb, GeoJsonPoint location) {
        this.id = generateId(nom, site);
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
        this.id = generateId(nom, site);
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
        this.id = generateId(nom, site);
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

    protected boolean addDepartement(Departement departement) {
        if (departement == null) {
            throw new NullPointerException("IUT cannot have a null departement");
        }
        if (this.departements == null) {
            this.departements = new ArrayList<>();
        }
        if (this.departements.contains(departement)) {
            return false;
        }
        this.departements.add(departement);
        return true;
    }

    protected boolean removeDepartement(Departement departement) {
        if (this.departements == null) {
            return false;
        }
        return this.departements.remove(departement);
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

    private static String generateId(String nom, String site) {
        /*
        If nom and site are given, use them first with a prefix.
        Otherwise use nom only if given
        Otherwise use random id
         */
        if (nom != null && !nom.isBlank()) {
            String genId;
            if (site != null && !site.isBlank()) {
                genId = "NS#" + nom + "#" + site;
            } else {
                genId = "N#" + nom;
            }
            return UUID.nameUUIDFromBytes(genId.getBytes()).toString();
        } else {
            return UUID.randomUUID().toString();
        }
    }
}
