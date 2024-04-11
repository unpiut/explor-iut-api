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
import explorateurIUT.configuration.TestDatasetConfig;
import explorateurIUT.model.projections.BUTSummary;
import explorateurIUT.model.projections.ParcoursBUTSummary;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author Remi Venant
 */
@DataMongoTest
@Import({MongoConfiguration.class, TestDatasetConfig.class})
@ActiveProfiles({"test", "mongo-test"})
public class BUTRepositoryTest {

    @Autowired
    private BUTRepository testedRepo;

    @Autowired
    private TestDatasetGenerator testDataset;

    public BUTRepositoryTest() {
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
     * Test of streamSummariesBy method, of class IUTRepository.
     */
    @Test
    public void testStreamSummariesBy() {
        final TestDatasetGenerator.TestInstances ti = this.testDataset.getTestInstances();

        List<String> expectedParcoursCode = Stream
                .of(ti.getButGB(), ti.getButINFO(), ti.getButMMI())
                .map(BUT::getParcours).flatMap(List::stream).map(ParcoursBUT::getCode)
                .toList();

        assertThat(this.testedRepo.streamSummariesBy()).as("All but summary are streamed")
                .flatMap(BUTSummary::getParcours).map(ParcoursBUTSummary::getCode)
                .containsExactlyInAnyOrderElementsOf(expectedParcoursCode);
    }

    @Test
    public void testFindByCodeIgnoreCase() {
        Optional<BUT> bOpt = this.testedRepo.findByCodeIgnoreCase("NOTABUT");
        assertThat(bOpt).as("Unknown code return empty optional").isEmpty();
        bOpt = this.testedRepo.findByCodeIgnoreCase("MMI");
        assertThat(bOpt).as("MMI code return proper but").isPresent()
                .contains(this.testDataset.getTestInstances().getButMMI());
    }
}
