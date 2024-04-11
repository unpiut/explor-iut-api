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
public class ExcelBUT {

    private static final Log LOG = LogFactory.getLog(ExcelBUT.class);

    private final String code;
    private final List<ExcelParcoursBUT> parcours;
    private String nom;
    private String filiere;
    private String description;
    private String metiers;
    private String urlFiche;
    private String urlFranceCompetence;
    private String universMetiers;

    public ExcelBUT(String code) {
        this.code = code;
        this.parcours = new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public List<ExcelParcoursBUT> getParcours() {
        return parcours;
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

    public String getFiliere() {
        return filiere;
    }

    public void setFiliere(String filiere) {
        if (filiere == null) {
            LOG.warn("Setting null filiere: do nothing");
            return;
        }
        if (this.filiere != null) {
            LOG.warn("Overiding filiere. old: " + this.filiere);
        }
        this.filiere = filiere;
    }

    public String getMetiers() {
        return metiers;
    }

    public void setMetiers(String metiers) {
        if (metiers == null) {
            LOG.warn("Setting null metiers: do nothing");
            return;
        }
        if (this.metiers != null) {
            LOG.warn("Overiding metiers. old: " + this.metiers);
        }
        this.metiers = metiers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null) {
            LOG.warn("Setting null description: do nothing");
            return;
        }
        if (this.description != null) {
            LOG.warn("Overiding description. old: " + this.description);
        }
        this.description = description;
    }

    public String getUrlFiche() {
        return urlFiche;
    }

    public void setUrlFiche(String urlFiche) {
        if (urlFiche == null) {
            LOG.warn("Setting null urlFiche: do nothing");
            return;
        }
        if (this.urlFiche != null) {
            LOG.warn("Overiding urlFiche. old: " + this.urlFiche);
        }
        this.urlFiche = urlFiche;
    }

    public String getUrlFranceCompetence() {
        return urlFranceCompetence;
    }

    public void setUrlFranceCompetence(String urlFranceCompetence) {
        if (urlFranceCompetence == null) {
            LOG.warn("Setting null urlFranceCompetence: do nothing");
            return;
        }
        if (this.urlFranceCompetence != null) {
            LOG.warn("Overiding urlFranceCompetence. old: " + this.urlFranceCompetence);
        }
        this.urlFranceCompetence = urlFranceCompetence;
    }

    public String getUniversMetiers() {
        return universMetiers;
    }

    public void setUniversMetiers(String universMetiers) {
        if (universMetiers == null) {
            LOG.warn("Setting null universMetiers: do nothing");
            return;
        }
        if (this.universMetiers != null) {
            LOG.warn("Overiding universMetiers. old: " + this.universMetiers);
        }
        this.universMetiers = universMetiers;
    }

    public void format(StringBuilder sb, String padding, int nbPads) {
        String pad = padding.repeat(nbPads);
        sb.append(pad).append("BUT ").append(code).append(System.lineSeparator());
        sb.append(pad).append("- nom : ").append(nom).append(System.lineSeparator());
        sb.append(pad).append("- filiere : ").append(filiere).append(System.lineSeparator());
        sb.append(pad).append("- description : ").append(description).append(System.lineSeparator());
        sb.append(pad).append("- urlFiche : ").append(urlFiche).append(System.lineSeparator());
        sb.append(pad).append("- urlFranceCompetence : ").append(urlFranceCompetence).append(System.lineSeparator());
        sb.append(pad).append("- universMetier : ").append(universMetiers).append(System.lineSeparator());
        sb.append(pad).append("- parcours : ").append(System.lineSeparator());
        for (ExcelParcoursBUT parcours : this.parcours) {
            parcours.format(sb, padding, nbPads + 1);
        }
    }
}
