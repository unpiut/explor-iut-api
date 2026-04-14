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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Julien Fourdan
 */
@Entity
public class PendingMail {

    @Id
    @GeneratedValue
    private Long id;

    @NotEmpty
    @OneToMany(mappedBy = "mail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PendingMailIUTRecipient> recipients = new HashSet<>();

    @NotBlank
    @Column(nullable = false)
    private String subject;

    @NotBlank
    @Column(nullable = false)
    private String body;

    @NotBlank
    @Column(nullable = false)
    private String replyTo;

    @NotBlank
    @Column(nullable = false)
    private String contactName;

    @CreatedDate
    private LocalDateTime creationDateTime;

    private LocalDateTime lastConfirmationMail;

    @OneToMany(mappedBy = "mail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PendingMailAttachement> attachements = new HashSet<>();

    protected PendingMail() {
    }

    public PendingMail(String subject, String body, String replyTo, String contactName) {
        this.subject = subject;
        this.body = body;
        this.replyTo = replyTo;
        this.contactName = contactName;
    }

    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public Set<PendingMailIUTRecipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<PendingMailIUTRecipient> recipients) {
        this.recipients = recipients;
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

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    protected void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public LocalDateTime getLastConfirmationMail() {
        return lastConfirmationMail;
    }

    protected void setLastConfirmationMail(LocalDateTime lastConfirmationMail) {
        this.lastConfirmationMail = lastConfirmationMail;
    }

    public String getContactName() {
        return this.contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Set<PendingMailAttachement> getAttachements() {
        return attachements;
    }

    public void setAttachements(Set<PendingMailAttachement> attachements) {
        this.attachements = attachements;
    }

    @Override
    public int hashCode() {
        if (this.id == null) {
            return super.hashCode();
        }
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PendingMail other = (PendingMail) obj;
        if (this.id == null || other.id == null) {
            return false;
        }
        return Objects.equals(this.id, other.id);
    }

}
