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
import explorateurIUT.model.views.DefaultView;
import explorateurIUT.model.views.IUTViews;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 *
 * @author Remi Venant
 */
@Document(collection = "Departements", language = "french")
public class Departement {

    @JsonView(IUTViews.Normal.class)
    @Id
    private String id;

    @JsonView(DefaultView.Never.class)
    @NotNull
    @DocumentReference(lazy = true)
    private IUT iut;

    @JsonView(IUTViews.Normal.class)
    @NotBlank
    @Indexed
    private String code;

    @JsonView(IUTViews.Details.class)
    private Set<ButAndParcoursDispenses> butDispenses = new HashSet<>();

    protected Departement() {
    }

    public Departement(IUT iut, String code) {
        this.iut = iut;
        this.code = code;
    }

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public IUT getIut() {
        return iut;
    }

    public void setIut(IUT iut) {
        this.iut = iut;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Set<ButAndParcoursDispenses> getButDispenses() {
        return Collections.unmodifiableSet(butDispenses);
    }

    public void setButDispenses(Set<ButAndParcoursDispenses> butDispenses) {
        this.butDispenses = butDispenses;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.id);
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
        final Departement other = (Departement) obj;
        return Objects.equals(this.id, other.id);
    }

}
