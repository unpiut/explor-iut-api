/*
 * Copyright (C) 2026 IUT Laval - Le Mans Université.
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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

/**
 *
 * @author Rémi Venant
 */
@Entity
public class PendingMailAttachement {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = false, fetch = FetchType.EAGER)
    private PendingMail mail;

    @NotNull
    @Column(nullable = false)
    private String fileName;

    private String contentType;

    @Lob
    private byte[] content;

    public PendingMailAttachement() {
    }

    public PendingMailAttachement(PendingMail mail, String fileName, String contentType, byte[] content) {
        this.mail = mail;
        this.fileName = fileName;
        this.contentType = contentType;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public PendingMail getMail() {
        return mail;
    }

    protected void setMail(PendingMail mail) {
        this.mail = mail;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public int hashCode() {
        if (this.id == null) {
            return super.hashCode();
        }
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.id);
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
        final PendingMailAttachement other = (PendingMailAttachement) obj;
        if (this.id == null || other.id == null) {
            return false;
        }
        return Objects.equals(this.id, other.id);
    }

}
