/*
 * Copyright (C) 2024 IUT Laval - Le Mans Université.
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
package explorateurIUT.services.mailManagement;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Remi Venant
 */
public class MailSendingRequest {

    @NotEmpty
    Collection<@NotNull @Pattern(regexp = "[abcdef0-9]{24}", flags = Pattern.Flag.CASE_INSENSITIVE) String> iutIds;
    @NotBlank
    String contactIdentity;
    @NotBlank
    String contactCompany;
    @NotBlank
    String contactFunction;
    @Email
    String contactMail;
    @NotBlank
    String subject;
    @NotBlank
    String body;
    List<@NotNull @Valid MailRequestAttachement> attachements;

    public MailSendingRequest() {
    }

    public MailSendingRequest(Collection<String> iutIds, String contactIdentity, String contactCompany, String contactFunction, String contactMail, String subject, String body, List<MailRequestAttachement> attachements) {
        this.iutIds = iutIds;
        this.contactIdentity = contactIdentity;
        this.contactCompany = contactCompany;
        this.contactFunction = contactFunction;
        this.contactMail = contactMail;
        this.subject = subject;
        this.body = body;
        this.attachements = attachements;
    }

    public Collection<String> getIutIds() {
        return iutIds;
    }

    public void setIutIds(Collection<String> iutIds) {
        this.iutIds = iutIds;
    }

    public String getContactIdentity() {
        return contactIdentity;
    }

    public void setContactIdentity(String contactIdentity) {
        this.contactIdentity = contactIdentity;
    }

    public String getContactCompany() {
        return contactCompany;
    }

    public void setContactCompany(String contactCompany) {
        this.contactCompany = contactCompany;
    }

    public String getContactFunction() {
        return contactFunction;
    }

    public void setContactFunction(String contactFunction) {
        this.contactFunction = contactFunction;
    }

    public String getContactMail() {
        return contactMail;
    }

    public void setContactMail(String contactMail) {
        this.contactMail = contactMail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<MailRequestAttachement> getAttachements() {
        return attachements;
    }

    public void setAttachements(List<MailRequestAttachement> attachements) {
        this.attachements = attachements;
    }

}
