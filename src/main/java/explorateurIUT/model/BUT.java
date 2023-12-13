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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 *
 * @author Remi Venant
 */
@Document(collection = "BUT", language = "french")
public class BUT {

    @JsonView(BUTViews.Normal.class)
    @Id
    private String id;

    @JsonView(BUTViews.Normal.class)
    @Indexed(unique = true)
    private String code;

    @JsonView(BUTViews.Normal.class)
    private String filiere;

    @JsonView(BUTViews.Details.class)
    private String description;

    @JsonView(BUTViews.Details.class)
    private String urlFiche;

    @JsonView(BUTViews.Details.class)
    @ReadOnlyProperty
    @DocumentReference(lookup = "{'but':?#{#self._id} }", lazy = true)
    private List<ParcoursBUT> parcours;

    protected BUT() {
    }

    public BUT(String code) {
        this.code = code;
    }

    public BUT(String code, String filiere, String description, String urlFiche) {
        this.code = code;
        this.filiere = filiere;
        this.description = description;
        this.urlFiche = urlFiche;
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

    public void setCode(String code) {
        this.code = code;
    }

    public String getFiliere() {
        return filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
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

    public List<ParcoursBUT> getParcours() {
        return parcours == null ? List.of() : Collections.unmodifiableList(parcours);
    }

    protected void setParcours(List<ParcoursBUT> parcours) {
        this.parcours = parcours;
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
        return "BUT{" + "id=" + id + ", code=" + code + '}';
    }

}
