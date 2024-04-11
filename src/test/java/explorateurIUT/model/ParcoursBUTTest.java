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

import explorateurIUT.configuration.MongoConfiguration;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author Remi Venant
 */
@DataMongoTest
@Import(MongoConfiguration.class)
@ActiveProfiles({"test", "mongo-test"})
public class ParcoursBUTTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    private BUT but1;
    private BUT but2;

    public ParcoursBUTTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        this.but1 = this.mongoTemplate.save(new BUT("BUT1", "le but 1", "la filiere", "les metiers",
                "la description", "l'url", "l'url france competence", "l'univers metier"));
        this.but2 = this.mongoTemplate.save(new BUT("BUT2", "le but 2", "la filiere", "les metiers",
                "la description", "l'url", "l'url france competence", "l'univers metier"));
    }

    @AfterEach
    public void tearDown() {
        this.mongoTemplate.remove(new BasicQuery("{}"), BUT.class);
        this.mongoTemplate.remove(new BasicQuery("{}"), ParcoursBUT.class);
    }

    /**
     * Test of getId method, of class ParcoursBUT.
     */
    @Test
    public void testParcoursBUTPersistence() {
        ParcoursBUT pb = new ParcoursBUT(but1, "PARC1", "Parcours 1");
        ParcoursBUT savedPb = this.mongoTemplate.save(pb);
        assertThat(savedPb).as("Saved pb not null").isNotNull();
        assertThat(savedPb.getId()).as("Saved pb has a not null id").isNotNull();

        ParcoursBUT retrievedPb = this.mongoTemplate.findById(savedPb.getId(), ParcoursBUT.class);
        assertThat(retrievedPb).as("Retrieved pb not null and not same as savedPB")
                .isNotNull().isNotSameAs(savedPb);
        assert retrievedPb != null;

        assertThat(retrievedPb).extracting("but", "code", "nom").as("Retrieved pb has valid properties")
                .containsExactly(savedPb.getBut(), savedPb.getCode(), savedPb.getNom());
    }

    @Test
    public void testParcoursExtractionFromBut() {
        ParcoursBUT pb1 = this.mongoTemplate.save(new ParcoursBUT(but1, "PARC1", "Parcours 1"));
        ParcoursBUT pb2 = this.mongoTemplate.save(new ParcoursBUT(but1, "PARC2", "Parcours 2"));
        this.mongoTemplate.save(new ParcoursBUT(but2, "PARC3", "Parcours 3"));

        BUT retrievedBut1 = this.mongoTemplate.findById(this.but1.getId(), BUT.class);
        assert retrievedBut1 != null;
        List<ParcoursBUT> parcoursBut = retrievedBut1.getParcours();
        assertThat(parcoursBut).as("Parcours but not null with size 2").isNotNull().hasSize(2);
        assertThat(parcoursBut).as("Parcours are the good ones for the but").containsExactlyInAnyOrder(pb1, pb2);
    }

    /**
     * Test of setId method, of class ParcoursBUT.
     */
    @Test
    public void testParcoursBUTValidation() {
        assertThatThrownBy(()
                -> this.mongoTemplate.save(new ParcoursBUT(null, "PARC1", "Parcours 1")))
                .as("Null but rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> this.mongoTemplate.save(new ParcoursBUT(but1, null, "Parcours 1")))
                .as("Null code rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> this.mongoTemplate.save(new ParcoursBUT(but1, "  \t  \n ", "Parcours 1")))
                .as("Blank code rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> this.mongoTemplate.save(new ParcoursBUT(but1, "PARC1", null)))
                .as("Null nom rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> this.mongoTemplate.save(new ParcoursBUT(but1, "PARC1", "  \t  \n ")))
                .as("Blank nom rejected")
                .isInstanceOf(ConstraintViolationException.class);
    }

    /**
     * Test of getBut method, of class ParcoursBUT.
     */
    @Test
    public void testUniqueCode() {
        this.mongoTemplate.save(new ParcoursBUT(but1, "PARC1", "Parcours 1"));
        assertThatThrownBy(()
                -> this.mongoTemplate.save(new ParcoursBUT(but2, "PARC1", "Parcours 2")))
                .as("Duplicated code rejected")
                .isInstanceOf(DuplicateKeyException.class);
    }

}
