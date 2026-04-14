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
package explorateurIUT.security.mailQuota.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

/**
 *
 * @author Rémi Venant
 */
@Embeddable
public class IPDepartementQuotaPK {

    @Column(nullable = false)
    private Long ipQuotaId;

    @Column(nullable = false)
    private String deptId;

    public IPDepartementQuotaPK() {
    }

    public IPDepartementQuotaPK(Long ipQuotaId, String deptId) {
        this.ipQuotaId = ipQuotaId;
        this.deptId = deptId;
    }

    public Long getIpQuotaId() {
        return ipQuotaId;
    }

    public void setIpQuotaId(Long ipQuotaId) {
        this.ipQuotaId = ipQuotaId;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.ipQuotaId);
        hash = 37 * hash + Objects.hashCode(this.deptId);
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
        final IPDepartementQuotaPK other = (IPDepartementQuotaPK) obj;
        if (!Objects.equals(this.deptId, other.deptId)) {
            return false;
        }
        return Objects.equals(this.ipQuotaId, other.ipQuotaId);
    }

    @Override
    public String toString() {
        return "IPDepartementQuotaPK{" + "ipQuotaId=" + ipQuotaId + ", deptId=" + deptId + '}';
    }

}
