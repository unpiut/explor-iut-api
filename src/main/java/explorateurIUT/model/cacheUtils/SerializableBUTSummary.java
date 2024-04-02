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
package explorateurIUT.model.cacheUtils;

import explorateurIUT.model.projections.BUTSummary;
import explorateurIUT.model.projections.ParcoursBUTSummary;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Remi Venant
 */
public class SerializableBUTSummary implements Serializable, BUTSummary {

    private final String id;
    private final String code;
    private final String nom;
    private final String filiere;
    private final String universMetiers;
    private final List<ParcoursBUTSummary> parcours;

    public SerializableBUTSummary(String id, String code, String nom, String filiere, String universMetiers, List<ParcoursBUTSummary> parcours) {
        this.id = id;
        this.code = code;
        this.nom = nom;
        this.filiere = filiere;
        this.universMetiers = universMetiers;
        this.parcours = parcours;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getNom() {
        return nom;
    }

    @Override
    public String getFiliere() {
        return this.filiere;
    }

    @Override
    public String getUniversMetiers() {
        return this.universMetiers;
    }

    @Override
    public List<ParcoursBUTSummary> getParcours() {
        return this.parcours;
    }

    public static BUTSummary fromBUTSummary(BUTSummary butSummary) {
        return new SerializableBUTSummary(butSummary.getId(), butSummary.getCode(),
                butSummary.getNom(), butSummary.getFiliere(), butSummary.getUniversMetiers(),
                butSummary.getParcours().stream()
                        .map(SerializableParcoursBUTSummary::fromParcoursBUTSummary)
                        .toList());
    }
}
