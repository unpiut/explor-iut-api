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
import java.util.UUID;

/**
 *
 * @author Remi Venant
 */
public class Departement {

    @JsonView(IUTViews.Normal.class)
    private String id; // will use iut.id - code as id

    @JsonView(DefaultView.Never.class)
    @NotNull
    private IUT iut;

    @JsonView(IUTViews.Normal.class)
    @NotBlank
    private String code; // not unique in general but unique per iut

    @JsonView(IUTViews.Details.class)
    private Set<ButAndParcoursDispenses> butDispenses = new HashSet<>();

    protected Departement() {
    }

    public Departement(IUT iut, String code) {
        this.id = generateId(iut, code);
        this.code = code;
        this.initIUT(iut);
    }

    private void initIUT(IUT iut) {
        this.iut = iut;
        this.id = generateId(iut, this.code);
        if (iut != null) {
            this.iut.addDepartement(this);
        }
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

    protected final void setIut(IUT iut) {
        if (iut == null) {
            throw new NullPointerException("departement must have an IUT");
        }
        if (iut.equals(this.iut)) {
            return;
        }
        IUT oldIut = this.iut;
        this.iut = iut;
        this.id = generateId(iut, this.code);
        if (oldIut != null) {
            oldIut.removeDepartement(this);
        }
        this.iut.addDepartement(this);
    }

    public String getCode() {
        return code;
    }

    protected void setCode(String code) {
        this.code = code;
        this.id = generateId(this.iut, code);
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

    private static String generateId(IUT iut, String code) {
        String ref = (iut != null ? iut.getId() : "NO_IUT") + "#" + code;
        return UUID.nameUUIDFromBytes(ref.getBytes()).toString();
    }
}
