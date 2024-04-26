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
package explorateurIUT.services;

import explorateurIUT.model.AppText;
import explorateurIUT.model.BUT;
import explorateurIUT.model.Departement;
import explorateurIUT.model.IUT;
import explorateurIUT.model.ParcoursBUT;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author rvenant
 */
@ActiveProfiles({"development", "app-test", "mongo-test", "ext-data"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class DataUploadServiceTest {

    @Value("classpath:data_sample.xlsx")
    private File dataSample;

    @Value("classpath:data_failing.xlsx")
    private File dataFailing;

    @Autowired
    private DataUploadService testSvc;

    @Autowired
    private MongoTemplate mongTemplate;

    public DataUploadServiceTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of uploadData method, of class DataUploadService.
     */
    @Test
    public void testUploadDataSuccess() throws Exception {
        final Query everyDocQuery = new Query(new Criteria());
        assertThat(this.mongTemplate.count(everyDocQuery, BUT.class)).as("Initial number of BUT is 0").isEqualTo(0);
        assertThat(this.mongTemplate.count(everyDocQuery, ParcoursBUT.class)).as("Initial number of ParcoursBUT is 0").isEqualTo(0);
        assertThat(this.mongTemplate.count(everyDocQuery, IUT.class)).as("Initial number of IUT inserted").isEqualTo(0);
        assertThat(this.mongTemplate.count(everyDocQuery, Departement.class)).as("Initial number of departement is 0").isEqualTo(0);
        assertThat(this.mongTemplate.count(everyDocQuery, AppText.class)).as("Initial number of AppText is 0").isEqualTo(0);

        try (InputStream is = new FileInputStream(this.dataSample)) {
            MockMultipartFile data = new MockMultipartFile("data.xlsx", is);
            this.testSvc.uploadData(data);
        }

        assertThat(this.mongTemplate.count(everyDocQuery, BUT.class)).as("Proper number of BUT inserted").isEqualTo(6);
        assertThat(this.mongTemplate.count(everyDocQuery, ParcoursBUT.class)).as("Proper number of ParcoursBUT inserted").isEqualTo(25);
        assertThat(this.mongTemplate.count(everyDocQuery, IUT.class)).as("Proper number of IUT inserted").isEqualTo(3);
        assertThat(this.mongTemplate.count(everyDocQuery, Departement.class)).as("Proper number of departement inserted").isEqualTo(7);
        assertThat(this.mongTemplate.count(everyDocQuery, AppText.class)).as("Proper number of AppText inserted").isEqualTo(6);
    }
    
    @Test
    public void testUploadDataFail() throws Exception {
        final Query everyDocQuery = new Query(new Criteria());
        assertThat(this.mongTemplate.count(everyDocQuery, BUT.class)).as("Initial number of BUT is 0").isEqualTo(0);
        assertThat(this.mongTemplate.count(everyDocQuery, ParcoursBUT.class)).as("Initial number of ParcoursBUT is 0").isEqualTo(0);
        assertThat(this.mongTemplate.count(everyDocQuery, IUT.class)).as("Initial number of IUT inserted").isEqualTo(0);
        assertThat(this.mongTemplate.count(everyDocQuery, Departement.class)).as("Initial number of departement is 0").isEqualTo(0);
        assertThat(this.mongTemplate.count(everyDocQuery, AppText.class)).as("Initial number of AppText is 0").isEqualTo(0);

        try (InputStream is = new FileInputStream(this.dataFailing)) {
            MockMultipartFile data = new MockMultipartFile("data.xlsx", is);
            assertThatThrownBy(()
                    -> this.testSvc.uploadData(data))
                    .as("UploadData failed on spring data exception (duplicate key)")
                    .isInstanceOf(DuplicateKeyException.class);
        }

        assertThat(this.mongTemplate.count(everyDocQuery, BUT.class)).as("Number of BUT still 0").isEqualTo(0);
        assertThat(this.mongTemplate.count(everyDocQuery, ParcoursBUT.class)).as("Number of ParcoursBUT still 0").isEqualTo(0);
        assertThat(this.mongTemplate.count(everyDocQuery, IUT.class)).as("Number of IUT still 0").isEqualTo(0);
        assertThat(this.mongTemplate.count(everyDocQuery, Departement.class)).as("Number of departement still 0").isEqualTo(0);
        assertThat(this.mongTemplate.count(everyDocQuery, AppText.class)).as("Number of AppText still 0").isEqualTo(0);
    }
    
}
