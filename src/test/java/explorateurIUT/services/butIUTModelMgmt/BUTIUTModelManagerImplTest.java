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
package explorateurIUT.services.butIUTModelMgmt;

import explorateurIUT.configuration.SimulatedBUTIUTModelManagerAndRepoConfig;
import explorateurIUT.model.BUT;
import explorateurIUT.model.TestDatasetGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author Rémi Venant
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(SimulatedBUTIUTModelManagerAndRepoConfig.class)
@ActiveProfiles("test")
public class BUTIUTModelManagerImplTest {

    @Configuration
    public static class NoConfiguration {
        // No automatic configuration
    }

    @Autowired
    private BUTIUTModelManager modelManager;

    @Autowired
    private TestDatasetGenerator testDataset;

    public BUTIUTModelManagerImplTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        this.testDataset.createDataset();
    }

    @AfterEach
    public void tearDown() {
        this.testDataset.clear();
    }

    /**
     * Test of isReady method, of class BUTIUTModelManagerImpl.
     */
    @Test
    public void testIsReady() {
        System.out.println("isReady");
        assertThat(this.modelManager.isReady()).as("Model loaded").isTrue();
    }

    /**
     * Test of getActiveModel method, of class BUTIUTModelManagerImpl.
     */
    @Test
    public void testGetActiveModel() {
        System.out.println("getActiveModel");
        BUTIUTModel activeModel = this.modelManager.getActiveModel();
        assertThat(activeModel).as("Active model not null").isNotNull();

        assertThat(activeModel.isReadOnly()).as("Active Model is readOnly").isTrue();

        assertThat(activeModel.getAppTextsById()).as("appTexts not null nor empty").isNotNull().isNotEmpty();
        assertThat(activeModel.getButsById()).as("Buts not null nor empty").isNotNull().isNotEmpty();
        assertThat(activeModel.getParcoursById()).as("Parcours not null nor empty").isNotNull().isNotEmpty();
        assertThat(activeModel.getIutsById()).as("IUTs not null nor empty").isNotNull().isNotEmpty();
        assertThat(activeModel.getDepartementsById()).as("Departements not null nor empty").isNotNull().isNotEmpty();
    }

    /**
     * Test of startNewModelCreation method, of class BUTIUTModelManagerImpl.
     */
    @Test
    public void testStartNewModelCreation() {
        System.out.println("startNewModelCreation");
        BUTIUTModel activeModel = this.modelManager.getActiveModel();
        BUTIUTModel newModel = this.modelManager.startNewModelCreation();
        assertThat(newModel).as("model for creation not null and not the same as active").isNotNull().isNotSameAs(activeModel);

        assertThat(newModel.isReadOnly()).as("Model for creation is not readOnly").isFalse();

        assertThat(newModel.getAppTextsById()).as("appTexts not null but empty").isNotNull().isEmpty();
        assertThat(newModel.getButsById()).as("Buts not null but empty").isNotNull().isEmpty();
        assertThat(newModel.getParcoursById()).as("Parcours not null but empty").isNotNull().isEmpty();
        assertThat(newModel.getIutsById()).as("IUTs not null but empty").isNotNull().isEmpty();
        assertThat(newModel.getDepartementsById()).as("Departements not null but empty").isNotNull().isEmpty();
    }

    /**
     * Test of replaceActiveModel method, of class BUTIUTModelManagerImpl.
     */
    @Test
    public void testReplaceActiveModel() {
        System.out.println("replaceActiveModel");
        BUTIUTModel activeModel = this.modelManager.getActiveModel();
        BUTIUTModel newModel = this.modelManager.startNewModelCreation();
        assertThat(newModel).as("model for creation not null and not the same as active").isNotNull().isNotSameAs(activeModel);

        newModel.saveBUT(new BUT("but1", "but1", "but1", "but1", "but1", "but1", "but1", "but1"));

        newModel.commit();

        activeModel = this.modelManager.getActiveModel();
        assertThat(newModel).as("model for creation is the same as active after commit").isSameAs(activeModel);
        assertThat(activeModel.isReadOnly()).as("Active model is readOnly after commit").isTrue();

    }

}
