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
import explorateurIUT.model.projections.DepartementSummary;
import explorateurIUT.model.projections.IUTMailOnly;
import explorateurIUT.model.projections.IUTSummary;
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
public class IUTRepositoryTest {

    @Configuration
    public static class NoConfiguration {
        // No automatic configuration
    }

    @Autowired
    private IUTRepository testedRepo;

    @Autowired
    private TestDatasetGenerator testDataset;

    public IUTRepositoryTest() {
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
        assertThat(this.testedRepo.streamSummariesBy()).as("All iut summary are streamed")
                .flatMap(IUTSummary::getDepartements).map(DepartementSummary::getCode).containsExactlyInAnyOrder(
                "Dept-Laval-GB", "Dept-Laval-MMI", "Dept-Laval-INFO",
                "Dept-Lannion-MMI", "Dept-Lannion-INFO");
    }

    @Test
    public void testStreamMailOnlyByIdInAndMelIsNotNull() {
        final TestDatasetGenerator.TestInstances ti = this.testDataset.getTestInstances();
        List<IUTMailOnly> res = this.testedRepo
                .streamMailOnlyByIdInAndMelIsNotNull(List.of(ti.getIutLannion().getId(), ti.getIutLaval().getId()))
                .toList();
        assertThat(res).as("Request two iut, got one email").hasSize(1);
        assertThat(res.getFirst().getMel()).as("Mail is Laval mail").isEqualTo(ti.getIutLaval().getMel());

        res = this.testedRepo
                .streamMailOnlyByIdInAndMelIsNotNull(List.of(ti.getIutLannion().getId()))
                .toList();
        assertThat(res).as("Request only one iut that has no mail is empty").isEmpty();

        res = this.testedRepo
                .streamMailOnlyByIdInAndMelIsNotNull(List.of(ti.getIutLaval().getId(), ti.getIutLaval().getId(), ti.getIutLaval().getId()))
                .toList();
        assertThat(res).as("Request three same iut, got one email").hasSize(1);
        assertThat(res.getFirst().getMel()).as("Mail is Laval mail").isEqualTo(ti.getIutLaval().getMel());
    }
}
