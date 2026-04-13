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
public class IUTTest {

    @Configuration
    public static class NoConfiguration {
        // No automatic configuration
    }

    @Autowired
    private BUTIUTModelManager modelMgr;

    private BUTIUTModel model;

    public IUTTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        this.model = this.modelMgr.startNewModelCreation();
    }

    @AfterEach
    public void tearDown() {
        this.model.rollback();
    }

    /**
     * Test of getId method, of class IUT.
     */
    @Test
    public void testIUTPersistence() {
        IUT iut = new IUT("nomIUT", "siteIUT", "region", "address", "0102030405",
                "iut.iut@iut.fr", "https://iut.fr", new GeoJsonPoint(42.1, 43.5));

        IUT savedIut = model.saveIUT(iut);
        assertThat(savedIut.getId()).as("created iut has an id").isNotNull();

        IUT retrievedIut = model.getIutsById().get(savedIut.getId());
        assertThat(retrievedIut).as("retrieve but not null")
                .isNotNull();
        assert retrievedIut != null;
        assertThat(retrievedIut).extracting("nom", "site", "region", "address", "tel", "mel", "urlWeb", "location")
                .containsExactly(iut.getNom(), iut.getSite(), iut.getRegion(), iut.getAddress(), iut.getTel(),
                        iut.getMel(), iut.getUrlWeb(), iut.getLocation());
        assertThat(retrievedIut.getDepartements()).as("Departements is not null but empty").isNotNull().isEmpty();
    }

    /**
     * Test of setId method, of class IUT.
     */
    @Test
    public void testIUTValidation() {
        assertThatThrownBy(()
                -> this.model.saveIUT(new IUT(null, "siteIUT", "region",
                        "address", "0102030405", "iut.iut@iut.fr", "https://iut.fr", new GeoJsonPoint(42.1, 43.5))))
                .as("Null nom rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> model.saveIUT(new IUT("  \t  \n ", "siteIUT", "region",
                        "address", "0102030405", "iut.iut@iut.fr", "https://iut.fr", new GeoJsonPoint(42.1, 43.5))))
                .as("Blank nom rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> model.saveIUT(new IUT("nomIUT", null, "region",
                        "address", "0102030405", "iut.iut@iut.fr", "https://iut.fr", new GeoJsonPoint(42.1, 43.5))))
                .as("Null site rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> model.saveIUT(new IUT("nomIUT", "  \t  \n ", "region",
                        "address", "0102030405", "iut.iut@iut.fr", "https://iut.fr", new GeoJsonPoint(42.1, 43.5))))
                .as("Blank site rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> model.saveIUT(new IUT("nomIUT", "siteIUT", "region",
                        "address", "0102030405", "invalid-email", "https://iut.fr", new GeoJsonPoint(42.1, 43.5))))
                .as("Invalid email rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> model.saveIUT(new IUT("nomIUT", "siteIUT", "region",
                        "address", "0102030405", "iut.iut@iut.fr", "https://iut.fr", null)))
                .as("Null location rejected")
                .isInstanceOf(ConstraintViolationException.class);
    }

}
