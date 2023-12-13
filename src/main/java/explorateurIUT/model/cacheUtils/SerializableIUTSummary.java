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

import explorateurIUT.model.projections.DepartementSummary;
import explorateurIUT.model.projections.IUTSummary;
import java.io.Serializable;
import java.util.List;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

/**
 *
 * @author Remi Venant
 */
public class SerializableIUTSummary implements Serializable, IUTSummary {

    private final String id;
    private final String nom;
    private final String site;
    private final GeoJsonPoint location;
    private final List<DepartementSummary> departements;

    public SerializableIUTSummary(String id, String nom, String site, GeoJsonPoint location, List<DepartementSummary> departements) {
        this.id = id;
        this.nom = nom;
        this.site = site;
        this.location = location;
        this.departements = departements;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getNom() {
        return this.nom;
    }

    @Override
    public String getSite() {
        return this.site;
    }

    @Override
    public GeoJsonPoint getLocation() {
        return this.location;
    }

    @Override
    public List<DepartementSummary> getDepartements() {
        return this.departements;
    }

    public static IUTSummary fromIUTSummary(IUTSummary iut) {
        return new SerializableIUTSummary(iut.getId(), iut.getNom(), iut.getSite(), iut.getLocation(),
                iut.getDepartements().stream().map(SerializableDepartementSummary::fromDepartementSummary).toList());
    }
}
