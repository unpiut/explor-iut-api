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
import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author Remi Venant
 */
public class SerializableDepartementSummary implements Serializable, DepartementSummary {

    private final String id;
    private final String code;
    private final Set<String> codesButDispenses;

    public SerializableDepartementSummary(String id, String code, Set<String> codesButDispenses) {
        this.id = id;
        this.code = code;
        this.codesButDispenses = codesButDispenses;
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
    public Set<String> getCodesButDispenses() {
        return this.codesButDispenses;
    }

    public static DepartementSummary fromDepartementSummary(DepartementSummary dept) {
        return new SerializableDepartementSummary(dept.getId(), dept.getCode(), dept.getCodesButDispenses());
    }
}
