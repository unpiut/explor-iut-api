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

import java.time.LocalDateTime;
import java.util.Objects;

/**
 *
 * @author rvenant
 */
public class IPDepartementQuota {

    private String deptId;

    private LocalDateTime started;

    private int counter;

    public IPDepartementQuota(String deptId, LocalDateTime started, int counter) {
        this.deptId = deptId;
        this.started = started;
        this.counter = counter;
    }

    public String getDeptId() {
        return deptId;
    }

    protected void setDeptId(String deptId) {
        this.deptId = deptId;
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
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.deptId);
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
        return Objects.equals(this.deptId, other.deptId);
    }

}
