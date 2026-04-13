/*
 * Copyright (C) 2026 IUT Laval - Le Mans Université.
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

import java.util.List;
import org.springframework.data.geo.Point;

/**
 *
 * @author Rémi Venant
 */
public class GeoJsonPoint extends Point {

    private static final long serialVersionUID = -8026303425147474002L;
    private static final String TYPE = "Point";

    public GeoJsonPoint(double x, double y) {
        super(x, y);
    }

    public GeoJsonPoint(Point point) {
        super(point);
    }

    public String getType() {
        return TYPE;
    }

    public List<Double> getCoordinates() {
        return List.of(this.getX(), this.getY());
    }
}
