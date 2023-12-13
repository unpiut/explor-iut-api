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
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 *
 * @author Remi Venant
 */
@Document(collection = "Parcoursdepts", language = "french")
public class ParcoursDept {

    @JsonView(IUTViews.Details.class)
    @Id
    private String id;

    @JsonView(DefaultView.Never.class)
    @DocumentReference(lazy = true)
    private IUT iut;

    @JsonView(DefaultView.Never.class)
    @DocumentReference(lazy = true)
    private Departement departement;

    @JsonView(DefaultView.Never.class)
    @DocumentReference(lazy = true)
    private ParcoursBUT parcoursBUT;

    @JsonView(IUTViews.Details.class)
    private String codeParcours;

    @JsonView(IUTViews.Details.class)
    private List<Alternance> alternances;

    protected ParcoursDept() {
    }

    public ParcoursDept(IUT iut, Departement departement, ParcoursBUT parcoursBUT) {
        this.iut = iut;
        this.departement = departement;
        this.parcoursBUT = parcoursBUT;
        this.codeParcours = parcoursBUT.getCode();
        this.alternances = new ArrayList<>();
    }

    public ParcoursDept(IUT iut, Departement departement, ParcoursBUT parcoursBUT, List<Alternance> alternances) {
        this.iut = iut;
        this.departement = departement;
        this.parcoursBUT = parcoursBUT;
        this.codeParcours = parcoursBUT.getCode();
        this.alternances = alternances;
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

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public ParcoursBUT getParcoursBUT() {
        return parcoursBUT;
    }

    public void setParcoursBUT(ParcoursBUT parcoursBUT) {
        this.parcoursBUT = parcoursBUT;
        this.codeParcours = parcoursBUT.getCode();
    }

    public String getCodeParcours() {
        return codeParcours;
    }

    public List<Alternance> getAlternances() {
        return alternances;
    }

    protected void setAlternances(List<Alternance> alternances) {
        this.alternances = alternances;
    }

}
