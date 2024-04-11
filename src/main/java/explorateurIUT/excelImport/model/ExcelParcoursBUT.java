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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Remi Venant
 */
public class ExcelParcoursBUT {

    private static final Log LOG = LogFactory.getLog(ExcelParcoursBUT.class);

    private final String code;
    private String nom;

    public ExcelParcoursBUT(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        if (nom == null) {
            LOG.warn("Setting null nom: do nothing");
            return;
        }
        if (this.nom != null) {
            LOG.warn("Overiding nom. old: " + this.nom);
        }
        this.nom = nom;
    }

    public void format(StringBuilder sb, String padding, int nbPads) {
        String pad = padding.repeat(nbPads);
        sb.append(pad).append("PARCOURS ").append(code).append(System.lineSeparator());
        sb.append(pad).append("- nom : ").append(nom).append(System.lineSeparator());
    }

}
