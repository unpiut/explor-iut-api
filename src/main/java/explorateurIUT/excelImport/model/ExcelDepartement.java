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
public class ExcelDepartement {

    private static final Log LOG = LogFactory.getLog(ExcelDepartement.class);

    private final String code;
    private final List<ExcelDiplomeDept> diplomes;
    private String mel;
    private String tel;
    private String url;
    private String contact;

    public ExcelDepartement(String code) {
        this.code = code;
        this.diplomes = new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public List<ExcelDiplomeDept> getDiplomes() {
        return diplomes;
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
            LOG.warn("Overiding contact. Old: " + this.contact + " | new: " + contact);
        }
        this.contact = contact;
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
            LOG.warn("Overiding mel. Old: " + this.mel + " | new: " + mel);
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
            LOG.warn("Overiding tel. Old: " + this.tel + " | new: " + tel);
        }
        this.tel = tel;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        if (url == null) {
            LOG.warn("Setting null url: do nothing");
            return;
        }
        if (this.url != null) {
            LOG.warn("Overiding url. Old: " + this.url + " | new: " + url);
        }
        this.url = url;
    }

    public void format(StringBuilder sb, String padding, int nbPads) {
        String pad = padding.repeat(nbPads);
        sb.append(pad).append("Dept ").append(code).append(System.lineSeparator());
        sb.append(pad).append("- mel : ").append(mel).append(System.lineSeparator());
        sb.append(pad).append("- tel : ").append(tel).append(System.lineSeparator());
        sb.append(pad).append("- url : ").append(url).append(System.lineSeparator());
        sb.append(pad).append("- contact : ").append(contact).append(System.lineSeparator());
        sb.append(pad).append("- parcours : ").append(System.lineSeparator());
        for (ExcelDiplomeDept d : this.diplomes) {
            d.format(sb, padding, nbPads + 1);
        }
    }

}
