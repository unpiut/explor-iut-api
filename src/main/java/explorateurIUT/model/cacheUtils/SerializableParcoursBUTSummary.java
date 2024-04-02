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

import explorateurIUT.model.projections.ParcoursBUTSummary;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Remi Venant
 */
public class SerializableParcoursBUTSummary implements Serializable, ParcoursBUTSummary {

    private final String id;
    private final String code;
    private final String nom;
    private final List<String> metiers;

    public SerializableParcoursBUTSummary(String id, String code, String nom, List<String> metiers) {
        this.id = id;
        this.code = code;
        this.nom = nom;
        this.metiers = metiers;
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
        return this.nom;
    }

    @Override
    public List<String> getMetiers() {
        return metiers;
    }

    public static ParcoursBUTSummary fromParcoursBUTSummary(ParcoursBUTSummary parcoursBut) {
        return new SerializableParcoursBUTSummary(parcoursBut.getId(), parcoursBut.getCode(), parcoursBut.getNom(), parcoursBut.getMetiers());
    }

}
