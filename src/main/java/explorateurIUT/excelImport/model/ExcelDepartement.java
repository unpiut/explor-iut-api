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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Remi Venant
 */
public class ExcelDepartement {

    private static final Log LOG = LogFactory.getLog(ExcelDepartement.class);

    private final String code;
    private final Map<String, Set<String>> parcoursByDiplomes;

    public ExcelDepartement(String code) {
        this.code = code;
        this.parcoursByDiplomes = new HashMap<>();
    }

    public String getCode() {
        return code;
    }

    public Map<String, Set<String>> getParcoursByDiplomes() {
        return Collections.unmodifiableMap(parcoursByDiplomes);
    }

    public void addParcours(String codeDiplome, String codeParcours) {
        this.parcoursByDiplomes.compute(codeDiplome, (k, v) -> {
            if (v != null) {
                if (!v.add(codeParcours)) {
                    LOG.warn("Adding duplicate Diplome-Parcours : " + codeDiplome + " - " + codeParcours);
                }
                return v;
            } else {
                HashSet<String> parcours = new HashSet<>();
                parcours.add(codeParcours);
                return parcours;
            }
        });
    }

    public void format(StringBuilder sb, String padding, int nbPads) {
        String pad = padding.repeat(nbPads);
        sb.append(pad).append("Dept ").append(code).append(System.lineSeparator());
        sb.append(pad).append("- parcours : ").append(System.lineSeparator());
        final String parcoursPad = padding.repeat(nbPads);
        this.parcoursByDiplomes.forEach((codeDiplome, codesParcours) -> {
            sb.append(parcoursPad).append("- ").append(codeDiplome).append(" : ").append(codesParcours.toString());
        });
    }

}
