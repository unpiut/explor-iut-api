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

import explorateurIUT.configuration.SimulatedBUTIUTModelManagerAndRepoConfig;
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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author Remi Venant
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(SimulatedBUTIUTModelManagerAndRepoConfig.class)
@ActiveProfiles("test")
public class BUTTest {

    @Configuration
    public static class NoConfiguration {
        // No automatic configuration
    }

    @Autowired
    private BUTIUTModelManager modelMgr;

    private BUTIUTModel model;

    public BUTTest() {
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
     * Test of getId method, of class BUT.
     */
    @Test
    public void testBUTPersistence() {
        BUT but = new BUT("LEBUT", "le but", "la filiere", "les metiers",
                "la description", "l'url", "l'url france competence", "l'univers metier");
        BUT savedBut = this.model.saveBUT(but);
        assertThat(savedBut.getId()).as("created but has an id").isNotNull();

        BUT retrievedBut = this.model.getButsById().get(savedBut.getId());
        assertThat(retrievedBut).as("retrieve but not null")
                .isNotNull();
        assert retrievedBut != null;
        assertThat(retrievedBut).extracting("code", "nom", "filiere", "metiers", "description", "urlFiche", "urlFranceCompetence", "universMetiers")
                .containsExactly(but.getCode(), but.getNom(), but.getFiliere(), but.getMetiers(),
                        but.getDescription(), but.getUrlFiche(), but.getUrlFranceCompetence(), but.getUniversMetiers());
        assertThat(retrievedBut.getParcours()).as("Parcours is not null but empty").isNotNull().isEmpty();
    }

    /**
     * Test of setId method, of class BUT.
     */
    @Test
    public void testBUTValidation() {
        assertThatThrownBy(()
                -> this.model.saveBUT(new BUT(null, "le but", "la filiere",
                        "les metiers", "la description", "l'url",
                        "l'url france competence", "l'univers metier")))
                .as("Null code rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> this.model.saveBUT(new BUT("  \t  \n ", "le but", "la filiere",
                        "les metiers", "la description", "l'url",
                        "l'url france competence", "l'univers metier")))
                .as("Blank code rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> this.model.saveBUT(new BUT("LEBUT", null, "la filiere",
                        "les metiers", "la description", "l'url",
                        "l'url france competence", "l'univers metier")))
                .as("Null nom rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> this.model.saveBUT(new BUT("LEBUT", "  \t  \n ", "la filiere",
                        "les metiers", "la description", "l'url",
                        "l'url france competence", "l'univers metier")))
                .as("Blank nom rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> this.model.saveBUT(new BUT("LEBUT", "le but", null,
                        "les metiers", "la description", "l'url",
                        "l'url france competence", "l'univers metier")))
                .as("Null filiere rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> this.model.saveBUT(new BUT("LEBUT", "le but", "  \t  \n ",
                        "les metiers", "la description", "l'url",
                        "l'url france competence", "l'univers metier")))
                .as("Blank filiere rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> this.model.saveBUT(new BUT("LEBUT", "le but", "la filiere",
                        null, "la description", "l'url",
                        "l'url france competence", "l'univers metier")))
                .as("Null metiers rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> this.model.saveBUT(new BUT("LEBUT", "le but", "la filiere",
                        "  \t  \n ", "la description", "l'url",
                        "l'url france competence", "l'univers metier")))
                .as("Blank metiers rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> this.model.saveBUT(new BUT("LEBUT", "le but", "la filiere",
                        "les metiers", null, "l'url",
                        "l'url france competence", "l'univers metier")))
                .as("Null description rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> this.model.saveBUT(new BUT("LEBUT", "le but", "la filiere",
                        "les metiers", "  \t  \n ", "l'url",
                        "l'url france competence", "l'univers metier")))
                .as("Blank description rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> this.model.saveBUT(new BUT("LEBUT", "le but", "la filiere",
                        "les metiers", "la description", "l'url",
                        "l'url france competence", null)))
                .as("Null universMetiers rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> this.model.saveBUT(new BUT("LEBUT", "le but", "la filiere",
                        "les metiers", "la description", "l'url",
                        "l'url france competence", "  \t  \n ")))
                .as("Blank universMetiers rejected")
                .isInstanceOf(ConstraintViolationException.class);
    }

    /**
     * Test of getCode method, of class BUT.
     */
    @Test
    public void testUniqueCode() {
        this.model.saveBUT(new BUT("LEBUT", "le but", "la filiere", "les metiers",
                "la description", "l'url", "l'url france competence", "l'univers metier"));
        assertThatThrownBy(()
                -> this.model.saveBUT(new BUT("LEBUT", "le but2", "la filiere2", "les metiers2",
                        "la description2", "l'url2", "l'url france competence2", "l'univers metier2")))
                .as("Duplicated code rejected")
                .isInstanceOf(DuplicateKeyException.class);
    }

}
