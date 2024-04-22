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

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author rvenant
 */
@Document(collection = "ipQuota")
public class IPQuota {

    @Id
    private String id;

    @NotBlank
    @Indexed(unique = true)
    private String ip;

    @NotNull
    private LocalDateTime started;

    @Min(1)
    private int counter;

    private Set<IPDepartementQuota> departementQuotas;

    protected IPQuota() {
    }

    public IPQuota(String ip, LocalDateTime globalCounterDatetime, int globalCounter) {
        this.ip = ip;
        this.started = globalCounterDatetime;
        this.counter = globalCounter;
    }

    public String getId() {
        return id;
    }

    protected void setId(String id) {
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
            quota = new IPDepartementQuota(departementId, newTime, counter);
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

}
