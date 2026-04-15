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
package explorateurIUT.services.butIUTModelMgmt.repositories;

import explorateurIUT.model.BUT;
import explorateurIUT.model.ButAndParcoursDispenses;
import explorateurIUT.model.Departement;
import explorateurIUT.model.GeoJsonPoint;
import explorateurIUT.model.IUT;
import explorateurIUT.model.ParcoursBUT;
import explorateurIUT.model.projections.BUTSummary;
import explorateurIUT.model.projections.DepartementSummary;
import explorateurIUT.model.projections.IUTMailOnly;
import explorateurIUT.model.projections.IUTSummary;
import explorateurIUT.model.projections.ParcoursBUTSummary;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Rémi Venant
 */
public class ProjectionsTools {

    public static ParcoursBUTSummary createParcoursSummary(ParcoursBUT parcours) {
        return new ParcoursBUTSummaryHandler(parcours);
    }

    public static BUTSummary createBUTSummary(BUT but) {
        return new BUTSummaryHandler(but);
    }

    public static DepartementSummary createDepartementSummary(Departement departement) {
        return new DepartementSummaryHandler(departement);
    }

    public static IUTSummary createIUTSummary(IUT iut) {
        return new IUTSummaryHandler(iut);
    }

    public static IUTMailOnly createIUTMailOnly(IUT iut) {
        return new IUTMailOnlyHandler(iut);
    }

    protected static class ParcoursBUTSummaryHandler implements ParcoursBUTSummary {

        private final ParcoursBUT parcours;

        public ParcoursBUTSummaryHandler(ParcoursBUT parcours) {
            this.parcours = parcours;
        }

        @Override
        public String getId() {
            return this.parcours.getId();
        }

        @Override
        public String getCode() {
            return this.parcours.getCode();
        }

        @Override
        public String getNom() {
            return this.parcours.getNom();
        }
    }

    protected static class BUTSummaryHandler implements BUTSummary {

        private final BUT but;

        public BUTSummaryHandler(BUT but) {
            this.but = but;
        }

        @Override
        public String getId() {
            return this.but.getId();
        }

        @Override
        public String getNom() {
            return this.but.getNom();
        }

        @Override
        public String getCode() {
            return this.but.getCode();
        }

        @Override
        public String getFiliere() {
            return this.but.getFiliere();
        }

        @Override
        public String getUniversMetiers() {
            return this.but.getUniversMetiers();
        }

        @Override
        public List<ParcoursBUTSummary> getParcours() {
            return this.but.getParcours().stream()
                    .map(ProjectionsTools::createParcoursSummary)
                    .toList();
        }

    }

    protected static class DepartementSummaryHandler implements DepartementSummary {

        private final Departement departement;

        public DepartementSummaryHandler(Departement departement) {
            this.departement = departement;
        }

        @Override
        public String getId() {
            return this.departement.getId();
        }

        @Override
        public String getCode() {
            return this.departement.getCode();
        }

        @Override
        public Set<ButAndParcoursDispenses> getButDispenses() {
            return this.departement.getButDispenses();
        }

    }

    protected static class IUTSummaryHandler implements IUTSummary {

        private final IUT iut;

        public IUTSummaryHandler(IUT iut) {
            this.iut = iut;
        }

        @Override
        public String getId() {
            return this.iut.getId();
        }

        @Override
        public String getNom() {
            return this.iut.getNom();
        }

        @Override
        public String getSite() {
            return this.iut.getSite();
        }

        @Override
        public String getRegion() {
            return this.iut.getRegion();
        }

        @Override
        public GeoJsonPoint getLocation() {
            return this.iut.getLocation();
        }

        @Override
        public List<DepartementSummary> getDepartements() {
            return this.iut.getDepartements().stream()
                    .map(ProjectionsTools::createDepartementSummary)
                    .toList();
        }

    }

    protected static class IUTMailOnlyHandler implements IUTMailOnly {

        private final IUT iut;

        public IUTMailOnlyHandler(IUT iut) {
            this.iut = iut;
        }

        @Override
        public String getId() {
            return this.iut.getId();
        }

        @Override
        public String getMel() {
            return this.iut.getMel();
        }
    }
}
