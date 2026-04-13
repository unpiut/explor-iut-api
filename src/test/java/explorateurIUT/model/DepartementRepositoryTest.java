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
import explorateurIUT.model.projections.DepartementCodesOfIUTId;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Remi Venant
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(TestDatasetConfig.class)
@ActiveProfiles("test")
public class DepartementRepositoryTest {

    @Configuration
    public static class NoConfiguration {
        // No automatic configuration
    }

    @Autowired
    private DepartementRepository testedRepo;

    @Autowired
    private TestDatasetGenerator testDataset;

    public DepartementRepositoryTest() {
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
     * Test of streamIUTIdByIdIn method, of class DepartementRepository.
     */
    @Test
    public void testStreamIUTIdByIdIn() {
        final TestDatasetGenerator.TestInstances ti = this.testDataset.getTestInstances();
        // All dept id
        List<String> allDeptsId = Stream.of(
                ti.getIutLaval().getDepartements().stream(),
                ti.getIutLannion().getDepartements().stream())
                .flatMap(Function.identity())
                .map(Departement::getId)
                .toList();

        List<DepartementCodesOfIUTId> iutIds = this.testedRepo.streamIUTIdByIdIn(allDeptsId).toList();
        assertThat(iutIds).as("Two iut id retrieves").hasSize(2)
                .map(DepartementCodesOfIUTId::getIut)
                .containsExactlyInAnyOrder(ti.getIutLannion().getId(), ti.getIutLaval().getId());
    }

}
