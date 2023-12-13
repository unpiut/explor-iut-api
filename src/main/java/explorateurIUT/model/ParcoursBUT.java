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
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
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
    @DocumentReference(lazy = true)
    private BUT but;

    @JsonView(BUTViews.Normal.class)
    @Indexed(unique = true)
    private String code;

    @JsonView(BUTViews.Normal.class)
    @TextIndexed(weight = 3.0f)
    private String nom;

    @JsonView(DefaultView.Never.class)
    @TextIndexed(weight = 1.0f)
    private String motsCles;

    @JsonView(BUTViews.Details.class)
    @TextIndexed(weight = 2.0f)
    private List<String> metiers;

    protected ParcoursBUT() {
    }

    public ParcoursBUT(BUT but, String code) {
        this.but = but;
        this.code = code;
    }

    public ParcoursBUT(BUT but, String code, String nom, String motsCles, List<String> metiers) {
        this.but = but;
        this.code = code;
        this.nom = nom;
        this.motsCles = motsCles;
        this.metiers = metiers;
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

    public String getMotsCles() {
        return motsCles;
    }

    public void setMotsCles(String motsCles) {
        this.motsCles = motsCles;
    }

    public List<String> getMetiers() {
        return metiers;
    }

    public void setMetiers(List<String> metiers) {
        this.metiers = metiers;
    }

}
