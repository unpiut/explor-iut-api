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
package explorateurIUT.model;

import explorateurIUT.configuration.TestDatasetConfig;
import explorateurIUT.services.butIUTModelMgmt.BUTIUTModel;
import explorateurIUT.services.butIUTModelMgmt.BUTIUTModelManager;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author Remi Venant
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(TestDatasetConfig.class)
@ActiveProfiles("test")
public class DepartementTest {

    @Configuration
    public static class NoConfiguration {
        // No automatic configuration
    }

    @Autowired
    private BUTIUTModelManager modelMgr;

    private BUTIUTModel model;

    private IUT iut1;
    private IUT iut2;

    public DepartementTest() {
    }

    @BeforeAll
    public static void setUpClass() {

    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
//        this.modelMgr = new BUTIUTModelManagerImpl(validator);
        this.model = this.modelMgr.startNewModelCreation();

        this.iut1 = this.model.saveIUT(new IUT("IUT1", "siteIUT1", "region",
                "address1", "0102030405", "iut1.iut@iut.fr", "https://iut.fr", new GeoJsonPoint(42.1, 43.5)));
        this.iut2 = this.model.saveIUT(new IUT("IUT2", "siteIUT", "region",
                "address2", "0102030405", "iut2.iut@iut.fr", "https://iut.fr", new GeoJsonPoint(44.1, 44.5)));
    }

    @AfterEach
    public void tearDown() {
        this.model.rollback();
    }

    @Test
    public void testDepartementPersistence() {
        Departement dept = new Departement(iut1, "DEPT");
        Departement savedDept = this.model.saveDepartement(dept);
        assertThat(savedDept).as("Saved dept not null").isNotNull();
        assertThat(savedDept.getId()).as("Saved dept has a not null id").isNotNull();

        Departement retrievedDept = this.model.getDepartementsById().get(savedDept.getId());
        assertThat(retrievedDept).as("Retrieved dept not null")
                .isNotNull();
        assert retrievedDept != null;

        assertThat(retrievedDept).extracting("iut", "code").as("Retrieved dept has valid properties")
                .containsExactly(savedDept.getIut(), savedDept.getCode());
    }

    @Test
    public void testDepartementExtractionFromIut() {
        Departement dept1 = this.model.saveDepartement(new Departement(iut1, "DEPT1"));
        Departement dept2 = this.model.saveDepartement(new Departement(iut1, "DEPT2"));
        this.model.saveDepartement(new Departement(iut2, "DEPT3"));

        IUT iut = this.model.getIutsById().get(this.iut1.getId());
        assert iut != null;
        List<Departement> deptIut = iut.getDepartements();
        assertThat(deptIut).as("Departements not null and size 2").isNotNull().hasSize(2);
        assertThat(deptIut).as("Departements are the good ones").containsExactlyInAnyOrder(dept1, dept2);
    }

    @Test
    public void testDepartementValidation() {
        assertThatThrownBy(()
                -> this.model.saveDepartement(new Departement(null, "DEPT1")))
                .as("Null iut rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> this.model.saveDepartement(new Departement(iut1, null)))
                .as("Null code rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> this.model.saveDepartement(new Departement(iut1, "  \t  \n ")))
                .as("Blank nom rejected")
                .isInstanceOf(ConstraintViolationException.class);
    }

}
