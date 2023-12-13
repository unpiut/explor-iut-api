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

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Remi Venant
 */
public class IUTFormationFilter {

    private static final Log LOG = LogFactory.getLog(IUTFormationFilter.class);

    public final static double DEFAULT_RADIUS_KM = 100D;
    public final static int MAX_FILTER_LIST_SIZE = 20;

    private String freeTextQuery;
    private Double latitude;
    private Double longitude;
    private Double radiusKm;
    private List<String> buts;
    private List<String> blockButs;
    private List<String> jobs;
    private boolean includeAllDepts;

    public IUTFormationFilter() {
    }

    public String getFreeTextQuery() {
        return freeTextQuery;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getRadiusKm() {
        return radiusKm;
    }

    public List<String> getButs() {
        return buts;
    }

    public List<String> getBlockButs() {
        return blockButs;
    }

    public List<String> getJobs() {
        return jobs;
    }

    public boolean isIncludeAllDepts() {
        return includeAllDepts;
    }

    @Override
    public String toString() {
        return "IUTFormationFilter{" + "freeTextQuery=" + freeTextQuery + ", latitude=" + latitude + ", longitude=" + longitude + ", radiusKm=" + radiusKm + ", buts=" + buts + ", blockButs=" + blockButs + ", jobs=" + jobs + ", includeAllDepts=" + includeAllDepts + '}';
    }

    protected void validate() throws IllegalArgumentException {
        if (this.freeTextQuery != null && this.freeTextQuery.isBlank()) {
            throw new IllegalArgumentException("Une recherche textuelle ne peut pas être vide et doit contenir au moins un caractère non blanc.");
        }
        if (this.latitude == null ^ this.longitude == null) {
            throw new IllegalArgumentException("Une latitude doit être fournie avec une longitude.");
        }
        if (this.radiusKm != null) {
            if (this.latitude == null) {
                throw new IllegalArgumentException("Un rayon de zone géographique de filtrage doit être accompagné d'une latitude et d'une longitude.");
            }
            if (this.radiusKm <= 0) {
                throw new IllegalArgumentException("Un rayon de zone géographique de filtrage doit être strictement positif.");
            }
        }
        if (this.buts != null && this.blockButs != null) {
            throw new IllegalArgumentException("Une liste de BUT ne peut pas être fournie conjointement avec une liste de BUT ouverts à l'alternance.");
        }
        if (this.buts != null) {
            if (this.buts.isEmpty()) {
                throw new IllegalArgumentException("Une liste de BUT fournie ne doit pas être vide");
            }
            if (this.buts.size() > MAX_FILTER_LIST_SIZE) {
                throw new IllegalArgumentException("Une liste de BUT fournie ne doit pas contenir plus de " + MAX_FILTER_LIST_SIZE + " occurences.");
            }
        }
        if (this.blockButs != null) {
            if (this.blockButs.isEmpty()) {
                throw new IllegalArgumentException("Une liste de BUT ouverts à l'alternance fournie ne doit pas être vide");
            }
            if (this.blockButs.size() > MAX_FILTER_LIST_SIZE) {
                throw new IllegalArgumentException("Une liste de BUT ouverts à l'alternance fournie ne doit pas contenir plus de " + MAX_FILTER_LIST_SIZE + " occurences.");
            }
        }
        if (this.jobs != null) {
            if (this.jobs.isEmpty()) {
                throw new IllegalArgumentException("Une liste de métiers fournie ne doit pas être vide");
            }
            if (this.jobs.size() > MAX_FILTER_LIST_SIZE) {
                throw new IllegalArgumentException("Une liste de métiers fournie ne doit pas contenir plus de " + MAX_FILTER_LIST_SIZE + " métiers.");
            }
        }
        if (this.freeTextQuery == null && this.latitude == null && this.buts == null
                && this.blockButs == null && this.jobs == null) {
            throw new IllegalArgumentException("Un filtre doit au moins contenir un élément de filtrage");
        }
    }

    public static class Builder {

        private final IUTFormationFilter filter = new IUTFormationFilter();
        private boolean built;

        private void checkNotBuiltYet() {
            if (this.built) {
                throw new IllegalAccessError("Filter already built");
            }
        }

        public Builder withFreeTextQuery(String query) {
            this.checkNotBuiltYet();
            LOG.debug("Add query to filter " + query);
            this.filter.freeTextQuery = query == null ? null : query.trim();
            return this;
        }

        public Builder withGPSFilter(Double latitude, Double longitude, Double radiusKm) {
            this.checkNotBuiltYet();
            LOG.debug("Add latitude, longitude and radius to filter: " + latitude + ", " + longitude + ", " + radiusKm);
            this.filter.latitude = latitude;
            this.filter.longitude = longitude;
            if ((latitude != null || longitude != null) && radiusKm == null) {
                this.filter.radiusKm = DEFAULT_RADIUS_KM;
            } else {
                this.filter.radiusKm = radiusKm;
            }
            return this;
        }

        public Builder withButs(List<String> buts) {
            this.checkNotBuiltYet();
            if (buts == null) {
                this.filter.buts = null;
            } else {
                LOG.debug("Add buts to filter");
                this.filter.buts = buts.stream()
                        .filter(b -> b != null && !b.isBlank())
                        .map(b -> b.trim()).toList();
                if (this.filter.buts.isEmpty()) {
                    this.filter.buts = null;
                }
            }
            return this;
        }

        public Builder withBlockButs(List<String> buts) {
            this.checkNotBuiltYet();
            if (buts == null) {
                this.filter.blockButs = null;
            } else {
                LOG.debug("Add block-buts to filter");
                this.filter.blockButs = buts.stream()
                        .filter(b -> b != null && !b.isBlank())
                        .map(b -> b.trim()).toList();
                if (this.filter.blockButs.isEmpty()) {
                    this.filter.blockButs = null;
                }
            }
            return this;
        }

        public Builder withJobs(List<String> jobs) {
            this.checkNotBuiltYet();
            if (jobs == null) {
                this.filter.jobs = null;
            } else {
                LOG.debug("Add jobs to filter");
                this.filter.jobs = jobs.stream()
                        .filter(j -> j != null && !j.isBlank())
                        .map(j -> j.trim()).toList();
                if (this.filter.jobs.isEmpty()) {
                    this.filter.jobs = null;
                }
            }
            return this;
        }

        public Builder withIncludeAllDepts(boolean includeAllDepts) {
            this.checkNotBuiltYet();
            LOG.debug("Add includeAllDepts to filter: " + includeAllDepts);
            this.filter.includeAllDepts = includeAllDepts;
            return this;
        }

        public IUTFormationFilter build(boolean validate) {
            if (validate) {
                this.filter.validate();
            }
            this.built = true;
            return this.filter;
        }

        public IUTFormationFilter build() {
            return this.build(true);
        }
    }
}
