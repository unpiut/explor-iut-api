/*
 * Copyright (C) 2024 IUT Laval - Le Mans Université.
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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 *
 * @author Remi Venant
 */
public class ButAndParcoursDispenses implements Serializable {

    @NotBlank
    @Indexed
    private String codeBut;

    @NotEmpty
    private Set<String> codeParcours;

    protected ButAndParcoursDispenses() {
        this.codeParcours = new HashSet<>();
    }

    public ButAndParcoursDispenses(String codeBut) {
        this.codeBut = codeBut;
        this.codeParcours = new HashSet<>();
    }

    public ButAndParcoursDispenses(String codeBut, Set<String> codeParcours) {
        this.codeBut = codeBut;
        this.codeParcours = codeParcours;
    }

    public String getCodeBut() {
        return codeBut;
    }

    public void setCodeBut(String codeBut) {
        this.codeBut = codeBut;
    }

    public Set<String> getCodeParcours() {
        return Collections.unmodifiableSet(codeParcours);
    }

    protected void setCodeParcours(Set<String> codeParcours) {
        this.codeParcours = codeParcours;
    }

    public boolean addParcours(ParcoursBUT parcours) {
        return this.codeParcours.add(parcours.getCode());
    }

    public boolean removeParcours(ParcoursBUT parcours) {
        return this.codeParcours.remove(parcours.getCode());
    }

    public boolean hasParcours() {
        return !this.codeParcours.isEmpty();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.codeBut);
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
        final ButAndParcoursDispenses other = (ButAndParcoursDispenses) obj;
        return Objects.equals(this.codeBut, other.codeBut);
    }

}
