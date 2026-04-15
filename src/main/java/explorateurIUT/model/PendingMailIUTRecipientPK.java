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

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Rémi Venant
 */
@Embeddable
public class PendingMailIUTRecipientPK implements Serializable {
    private static final Long serialVersionUID = 1L;

    @Column(nullable = false)
    private Long mailId;

    @Column(nullable = false)
    private String mailAddress;

    protected PendingMailIUTRecipientPK() {
    }

    public PendingMailIUTRecipientPK(Long mailId, String mailAddress) {
        this.mailId = mailId;
        this.mailAddress = mailAddress;
    }

    public Long getMailId() {
        return mailId;
    }

    protected void setMailId(Long mailId) {
        this.mailId = mailId;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    protected void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.mailId);
        hash = 79 * hash + Objects.hashCode(this.mailAddress);
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
        final PendingMailIUTRecipientPK other = (PendingMailIUTRecipientPK) obj;
        if (!Objects.equals(this.mailAddress, other.mailAddress)) {
            return false;
        }
        return Objects.equals(this.mailId, other.mailId);
    }

    @Override
    public String toString() {
        return "PendingMailIUTRecipientPK{" + "mailId=" + mailId + ", mailAddress=" + mailAddress + '}';
    }

}
