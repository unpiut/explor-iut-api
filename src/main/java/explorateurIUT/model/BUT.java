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
import explorateurIUT.model.views.BUTViews;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Remi Venant
 */
public class BUT {

    @JsonView(BUTViews.Normal.class)
    private String id; // will contain a ref to code

    @JsonView(BUTViews.Normal.class)
    @NotBlank
    private String code; // unique, will be used at id

    @JsonView(BUTViews.Normal.class)
    @NotBlank
    private String nom;

    @JsonView(BUTViews.Normal.class)
    @NotBlank
    private String filiere;

    @JsonView(BUTViews.Details.class)
    @NotBlank
    private String metiers;

    @JsonView(BUTViews.Details.class)
    @NotBlank
    private String description;

    @JsonView(BUTViews.Details.class)
    private String urlFiche;

    @JsonView(BUTViews.Details.class)
    private String urlFranceCompetence;

    @JsonView(BUTViews.Normal.class)
    @NotBlank
    private String universMetiers;

    @JsonView(BUTViews.Details.class)
    private List<ParcoursBUT> parcours;

    protected BUT() {
    }

    public BUT(String code) {
        this.code = code;
    }

    public BUT(String code, String nom, String filiere, String metiers, String description, String urlFiche, String urlFranceCompetence, String universMetiers) {
        this.id = generateId(code);
        this.code = code;
        this.nom = nom;
        this.filiere = filiere;
        this.metiers = metiers;
        this.description = description;
        this.urlFiche = urlFiche;
        this.urlFranceCompetence = urlFranceCompetence;
        this.universMetiers = universMetiers;
    }

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    protected void setCode(String code) {
        this.code = code;
        this.id = generateId(code);
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getFiliere() {
        return filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }

    public String getMetiers() {
        return metiers;
    }

    public void setMetiers(String metiers) {
        this.metiers = metiers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlFiche() {
        return urlFiche;
    }

    public void setUrlFiche(String urlFiche) {
        this.urlFiche = urlFiche;
    }

    public String getUrlFranceCompetence() {
        return urlFranceCompetence;
    }

    public void setUrlFranceCompetence(String urlFranceCompetence) {
        this.urlFranceCompetence = urlFranceCompetence;
    }

    public String getUniversMetiers() {
        return universMetiers;
    }

    public void setUniversMetiers(String universMetiers) {
        this.universMetiers = universMetiers;
    }

    public List<ParcoursBUT> getParcours() {
        return parcours == null ? List.of() : Collections.unmodifiableList(parcours);
    }

    protected void setParcours(List<ParcoursBUT> parcours) {
        this.parcours = parcours;
    }

    protected boolean addParcours(ParcoursBUT parcours) {
        if (parcours == null) {
            throw new NullPointerException("BUT cannot have a null parcours");
        }
        if (this.parcours == null) {
            this.parcours = new ArrayList<>();
        }
        if (this.parcours.contains(parcours)) {
            return false;
        }
        this.parcours.add(parcours);
        return true;
    }

    protected boolean removeParcours(ParcoursBUT parcours) {
        if (this.parcours == null) {
            return false;
        }
        return this.parcours.remove(parcours);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
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
        final BUT other = (BUT) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "BUT{" + "id=" + id + ", code=" + code + ", nom=" + nom + '}';
    }

    private static String generateId(String code) {
        return code != null
                ? UUID.nameUUIDFromBytes(code.getBytes()).toString()
                : UUID.randomUUID().toString();
    }
}
