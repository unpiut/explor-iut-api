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
public class ExcelAnneeAlt {

    private static final Log LOG = LogFactory.getLog(ExcelAnneeAlt.class);

    private final int annee;
    private String mel;
    private String tel;
    private String contact;
    private String urlCal;

    public ExcelAnneeAlt(int annee) {
        this.annee = annee;
    }

    public int getAnnee() {
        return annee;
    }

    public String getMel() {
        return mel;
    }

    public void setMel(String mel) {
        if (mel == null) {
            LOG.warn("Setting null mel: do nothing");
            return;
        }
        if (this.mel != null) {
            LOG.warn("Overiding mel");
        }
        this.mel = mel;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        if (tel == null) {
            LOG.warn("Setting null tel: do nothing");
            return;
        }
        if (this.tel != null) {
            LOG.warn("Overiding tel");
        }
        this.tel = tel;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        if (contact == null) {
            LOG.warn("Setting null contact: do nothing");
            return;
        }
        if (this.contact != null) {
            LOG.warn("Overiding contact. Old: " + this.contact + " | New: " + contact);
        }
        this.contact = contact;
    }

    public String getUrlCal() {
        return urlCal;
    }

    public void setUrlCal(String urlCal) {
        if (urlCal == null) {
            LOG.warn("Setting null urlCal: do nothing");
            return;
        }
        if (this.urlCal != null) {
            LOG.warn("Overiding urlCal");
        }
        this.urlCal = urlCal;
    }

    public void format(StringBuilder sb, String padding, int nbPads) {
        String pad = padding.repeat(nbPads);
        sb.append(pad).append("AnneeAlt ").append(annee).append(System.lineSeparator());
        sb.append(pad).append("- mel : ").append(mel).append(System.lineSeparator());
        sb.append(pad).append("- tel : ").append(tel).append(System.lineSeparator());
        sb.append(pad).append("- contact : ").append(contact).append(System.lineSeparator());
        sb.append(pad).append("- urlCal : ").append(urlCal).append(System.lineSeparator());
    }

}
