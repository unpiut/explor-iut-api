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
package explorateurIUT.excelImport.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Remi Venant
 */
public class ExcelDiplomeDept {

    private static final Log LOG = LogFactory.getLog(ExcelDiplomeDept.class);

    private final String code;
    private final List<ExcelParcoursDeptDip> parcours;

    public ExcelDiplomeDept(String code) {
        this.code = code;
        this.parcours = new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public List<ExcelParcoursDeptDip> getParcours() {
        return parcours;
    }

    public void format(StringBuilder sb, String padding, int nbPads) {
        String pad = padding.repeat(nbPads);
        sb.append(pad).append("Diploma ").append(code).append(System.lineSeparator());
        sb.append(pad).append("- parcours : ").append(System.lineSeparator());
        for (ExcelParcoursDeptDip p : this.parcours) {
            p.format(sb, padding, nbPads + 1);
        }
    }

}
