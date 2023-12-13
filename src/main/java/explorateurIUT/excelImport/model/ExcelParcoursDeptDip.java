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
public class ExcelParcoursDeptDip {

    private static final Log LOG = LogFactory.getLog(ExcelParcoursDeptDip.class);

    private final String code;
    private final List<ExcelAnneeAlt> anneesAlt;

    public ExcelParcoursDeptDip(String code) {
        this.code = code;
        this.anneesAlt = new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public List<ExcelAnneeAlt> getAnneesAlt() {
        return anneesAlt;
    }

    public void format(StringBuilder sb, String padding, int nbPads) {
        String pad = padding.repeat(nbPads);
        sb.append(pad).append("Parcours ").append(code).append(System.lineSeparator());
        sb.append(pad).append("- annees alt : ").append(System.lineSeparator());
        for (ExcelAnneeAlt aa : this.anneesAlt) {
            aa.format(sb, padding, nbPads + 1);
        }

    }

}
