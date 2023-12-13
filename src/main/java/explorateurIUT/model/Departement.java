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
import jakarta.validation.constraints.Email;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
    @DocumentReference(lazy = true)
    private IUT iut;

    @JsonView(IUTViews.Normal.class)
    @Indexed
    private String code;

    @JsonView(IUTViews.Details.class)
    private String tel;

    @JsonView(IUTViews.Details.class)
    @Email
    private String mel;

    @JsonView(IUTViews.Details.class)
    private String urlWeb;

    @JsonView(IUTViews.Details.class)
    @DocumentReference(lookup = "{'departement':?#{#self._id} }", lazy = true)
    private List<ParcoursDept> parcours;

    @JsonView(DefaultView.Never.class)
    @DocumentReference(lazy = true)
    private Set<BUT> butDispenses = new HashSet<>();

    @JsonView(IUTViews.Details.class)
    private Set<String> codesButDispenses = new HashSet<>();

    protected Departement() {
    }

    public Departement(IUT iut, String code) {
        this.iut = iut;
        this.code = code;
    }

    public Departement(IUT iut, String code, String tel, String mel, String urlWeb) {
        this.iut = iut;
        this.code = code;
        this.tel = tel;
        this.mel = mel;
        this.urlWeb = urlWeb;
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

    public List<ParcoursDept> getParcours() {
        return parcours == null ? List.of() : Collections.unmodifiableList(parcours);
    }

    protected void setParcours(List<ParcoursDept> parcours) {
        this.parcours = parcours;
    }

    public Set<BUT> getButDispenses() {
        return Collections.unmodifiableSet(butDispenses);
    }

    public boolean addButDispense(BUT but) {
        if (butDispenses.add(but)) {
            this.codesButDispenses.add(but.getCode());
            return true;
        }
        return false;
    }

    public boolean removeButDispense(BUT but) {
        if (butDispenses.remove(but)) {
            this.codesButDispenses.remove(but.getCode());
            return true;
        }
        return false;
    }

    protected void setButDispenses(Set<BUT> butDispenses) {
        this.butDispenses = butDispenses;
        this.codesButDispenses = this.butDispenses.stream().map(BUT::getCode).collect(Collectors.toSet());
    }

    public Set<String> getCodesButDispenses() {
        return Collections.unmodifiableSet(codesButDispenses);
    }

}
