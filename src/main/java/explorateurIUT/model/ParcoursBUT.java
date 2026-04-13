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
import explorateurIUT.model.views.DefaultView;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Remi Venant
 */
public class ParcoursBUT {

    @JsonView(BUTViews.Normal.class)
    private String id; // will contain a ref to code

    @JsonView(DefaultView.Never.class)
    @NotNull
    private BUT but;

    @JsonView(BUTViews.Normal.class)
    @NotBlank
    private String code; // Unique, will be used at id

    @JsonView(BUTViews.Normal.class)
    @NotBlank
    private String nom;

    protected ParcoursBUT() {
    }

    public ParcoursBUT(BUT but, String code, String nom) {
        this.id = generateId(code);
        this.code = code;
        this.nom = nom;
        this.initBUT(but);
    }

    public ParcoursBUT(BUT but, String code) {
        this(but, code, null);
    }

    private void initBUT(BUT but) {
        this.but = but;
        if (but != null) {
            this.but.addParcours(this);
        }
    }

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public BUT getBut() {
        return but;
    }

    protected final void setBut(BUT but) {
        if (but == null) {
            throw new NullPointerException("Parcours must have a BUT");
        }
        if (but.equals(this.but)) {
            return;
        }
        BUT oldBut = this.but;
        this.but = but;
        if (oldBut != null) {
            oldBut.removeParcours(this);
        }
        this.but.addParcours(this);
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.id);
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
        final ParcoursBUT other = (ParcoursBUT) obj;
        return Objects.equals(this.id, other.id);
    }

    private static String generateId(String code) {
        return code != null
                ? UUID.nameUUIDFromBytes(code.getBytes()).toString()
                : UUID.randomUUID().toString();
    }
}
