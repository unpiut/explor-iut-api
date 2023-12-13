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

import java.util.List;
import java.util.Objects;
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
    private String motsCles;
    private List<String> metiers;

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

    public String getMotsCles() {
        return motsCles;
    }

    public void setMotsCles(String motsCles) {
        if (motsCles == null) {
            LOG.warn("Setting null motsCles: do nothing");
            return;
        }
        if (this.motsCles != null) {
            LOG.warn("Overiding motsCles. old: " + this.motsCles);
        }
        this.motsCles = motsCles;
    }

    public List<String> getMetiers() {
        return metiers;
    }

    public void setMetiers(List<String> metiers) {
        if (metiers == null) {
            LOG.warn("Setting null metiers: do nothing");
            return;
        }
        if (this.metiers != null) {
            LOG.warn("Overiding metiers. old: " + this.metiers);
        }
        this.metiers = metiers;
    }

    public void format(StringBuilder sb, String padding, int nbPads) {
        String pad = padding.repeat(nbPads);
        sb.append(pad).append("PARCOURS ").append(code).append(System.lineSeparator());
        sb.append(pad).append("- nom : ").append(nom).append(System.lineSeparator());
        sb.append(pad).append("- mots-cles : ").append(motsCles).append(System.lineSeparator());
        sb.append(pad).append("- metiers : ").append(Objects.toString(metiers)).append(System.lineSeparator());
    }

}
