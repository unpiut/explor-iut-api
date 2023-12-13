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
package explorateurIUT.model;

/**
 *
 * @author Remi Venant
 */
public class Alternance {

    private int annee;

    private String mel;

    private String tel;

    private String contact;

    private String urlCalendrier;

    protected Alternance() {
    }

    public Alternance(int annee) {
        this.annee = annee;
    }

    public Alternance(int annee, String mel, String tel, String contact, String urlCalendrier) {
        this.annee = annee;
        this.mel = mel;
        this.tel = tel;
        this.contact = contact;
        this.urlCalendrier = urlCalendrier;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public String getMel() {
        return mel;
    }

    public void setMel(String mel) {
        this.mel = mel;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getUrlCalendrier() {
        return urlCalendrier;
    }

    public void setUrlCalendrier(String urlCalendrier) {
        this.urlCalendrier = urlCalendrier;
    }

}
