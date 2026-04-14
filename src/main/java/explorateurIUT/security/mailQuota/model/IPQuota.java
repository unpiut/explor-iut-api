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
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author rvenant
 */
@Entity
public class IPQuota {

    @Id
    private Long id;

    @NotBlank
    @Column(length = 39, nullable = false, unique = true)
    private String ip;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime started;

    @Min(1)
    private int counter;

    @OneToMany(mappedBy = "ipQuota", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<IPDepartementQuota> departementQuotas = new HashSet<>();

    protected IPQuota() {
    }

    public IPQuota(String ip, LocalDateTime globalCounterDatetime, int globalCounter) {
        this.ip = ip;
        this.started = globalCounterDatetime;
        this.counter = globalCounter;
    }

    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    protected void setIp(String ip) {
        this.ip = ip;
    }

    public LocalDateTime getStarted() {
        return started;
    }

    public void setStarted(LocalDateTime started) {
        this.started = started;
    }

    public int getCounter() {
        return counter;
    }

    protected void setCounter(int counter) {
        this.counter = counter;
    }

    public int incrementCounter(int counter, LocalDateTime minimumTime, LocalDateTime newTime) {
        if (this.started.isBefore(minimumTime)) {
            this.started = newTime;
            this.counter = counter;
        } else {
            this.counter += counter;
        }
        return this.counter;
    }

    public Set<IPDepartementQuota> getDepartementQuotas() {
        return departementQuotas == null ? Collections.EMPTY_SET : Collections.unmodifiableSet(departementQuotas);
    }

    protected void setDepartementQuotas(Set<IPDepartementQuota> departementQuotas) {
        this.departementQuotas = departementQuotas;
    }

    public IPDepartementQuota updateOrCreateDepartementQuota(String departementId, int counter, LocalDateTime minimumTime, LocalDateTime newTime) {
        // Retrieve the dept quota if it exists
        IPDepartementQuota quota = null;
        if (this.departementQuotas == null) {
            this.departementQuotas = new HashSet<>();
        } else {
            quota = this.departementQuotas.stream()
                    .filter(q -> q.getDeptId().equals(departementId))
                    .findFirst()
                    .orElse(null);
        }
        // If no quota, create a new one and add it to the set
        if (quota == null) {
            quota = new IPDepartementQuota(this, departementId, newTime, counter);
            this.departementQuotas.add(quota);
        } else {
            // if quota exist, update it
            if (quota.getStarted().isBefore(minimumTime)) {
                quota.setStarted(newTime);
                quota.setCounter(counter);
            } else {
                quota.setCounter(quota.getCounter() + counter);
            }
        }
        return quota;
    }

    @Override
    public int hashCode() {
        if (this.id == null) {
            return super.hashCode();
        }
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.id);
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
        final IPQuota other = (IPQuota) obj;
        if (this.id == null || other.id == null) {
            return false;
        }
        return Objects.equals(this.id, other.id);
    }

}
