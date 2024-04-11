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
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 *
 * @author Remi Venant
 */
@Document(collection = "ParcoursBUT", language = "french")
public class ParcoursBUT {

    @JsonView(BUTViews.Normal.class)
    @Id
    private String id;

    @JsonView(DefaultView.Never.class)
    @NotNull
    @DocumentReference(lazy = true)
    private BUT but;

    @JsonView(BUTViews.Normal.class)
    @NotBlank
    @Indexed(unique = true)
    private String code;

    @JsonView(BUTViews.Normal.class)
    @NotBlank
    private String nom;

    protected ParcoursBUT() {
    }

    public ParcoursBUT(BUT but, String code) {
        this.but = but;
        this.code = code;
    }

    public ParcoursBUT(BUT but, String code, String nom) {
        this.but = but;
        this.code = code;
        this.nom = nom;
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

    public void setBut(BUT but) {
        this.but = but;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

}
