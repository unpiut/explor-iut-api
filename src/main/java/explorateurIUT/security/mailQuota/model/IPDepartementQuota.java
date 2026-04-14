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
package explorateurIUT.security.mailQuota.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 *
 * @author rvenant
 */
@Entity
public class IPDepartementQuota {

    @EmbeddedId
    private IPDepartementQuotaPK id;

    @NotNull
    @MapsId("ipQuotaId")
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(insertable = false, updatable = false)
    private IPQuota ipQuota;

    private LocalDateTime started;

    private int counter;

    protected IPDepartementQuota() {
    }

    protected IPDepartementQuota(IPQuota ipQuota, String deptId, LocalDateTime started, int counter) {
        this.id = new IPDepartementQuotaPK(ipQuota.getId(), deptId);
        this.ipQuota = ipQuota;
        this.started = started;
        this.counter = counter;
    }

    public IPDepartementQuotaPK getId() {
        return id;
    }

    protected void setId(IPDepartementQuotaPK id) {
        this.id = id;
    }

    public IPQuota getIpQuota() {
        return ipQuota;
    }

    protected void setIpQuota(IPQuota ipQuota) {
        this.ipQuota = ipQuota;
    }

    public String getDeptId() {
        return this.id.getDeptId();
    }

    protected void setDeptId(String deptId) {
        this.id.setDeptId(deptId);
    }

    public LocalDateTime getStarted() {
        return started;
    }

    protected void setStarted(LocalDateTime started) {
        this.started = started;
    }

    public int getCounter() {
        return counter;
    }

    protected void setCounter(int counter) {
        this.counter = counter;
    }

    public int incrementCounter(int delta) {
        this.counter += delta;
        return this.counter;
    }

    @Override
    public int hashCode() {
        if (this.id == null) {
            return super.hashCode();
        }
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.id);
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
        final IPDepartementQuota other = (IPDepartementQuota) obj;
        if (this.id == null || other.id == null) {
            return false;
        }
        return Objects.equals(this.id, other.id);
    }

}
