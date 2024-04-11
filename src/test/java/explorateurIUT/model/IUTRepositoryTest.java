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
import explorateurIUT.model.projections.DepartementSummary;
import explorateurIUT.model.projections.IUTSummary;
import java.util.Collection;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author Remi Venant
 */
@DataMongoTest
@Import({MongoConfiguration.class, TestDatasetConfig.class})
@ActiveProfiles({"test", "mongo-test"})
public class IUTRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

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
    public void tStreamSummariesByFilter() {
        final TestDatasetGenerator.TestInstances ti = this.testDataset.getTestInstances();
        IUTFormationFilter filter;
        Collection<IUTSummary> iutFound;
        IUTSummary iut;
        // free query
        filter = IUTFormationFilter.createBuilder().withFreeTextQuery("laboratoire microbiologie").build();
        iutFound = this.testedRepo.streamSummariesByFilter(filter);
        assertThat(iutFound).as("Filter Free text: 1 iut found").hasSize(1);
        iut = iutFound.stream().findFirst().get();
        assertThat(iut.getId()).as("Filter Free text: IUT found is Laval").isEqualTo(ti.getIutLaval().getId());
        assertThat(iut.getDepartements()).as("Filter Free text: 1 dept is proposed: GB").hasSize(1)
                .map(DepartementSummary::getCode).allMatch(c -> c.equals("Dept-Laval-GB"));

        // geo
        filter = IUTFormationFilter.createBuilder().withGPSFilter(
                ti.getIutLaval().getLocation().getY(),
                ti.getIutLaval().getLocation().getX(),
                50D).build();
        iutFound = this.testedRepo.streamSummariesByFilter(filter);
        assertThat(iutFound).as("Filter GPS: 1 iut found").hasSize(1);
        iut = iutFound.stream().findFirst().get();
        assertThat(iut.getId()).as("Filter GPS: IUT found is Laval").isEqualTo(ti.getIutLaval().getId());
        assertThat(iut.getDepartements()).as("Filter GPS: 1 dept is proposed: GB").hasSize(3)
                .map(DepartementSummary::getCode).containsExactlyInAnyOrder("Dept-Laval-GB", "Dept-Laval-MMI", "Dept-Laval-INFO");

        // region
        filter = IUTFormationFilter.createBuilder().withRegions(List.of("PAYS DE LOIRE")).build();
        iutFound = this.testedRepo.streamSummariesByFilter(filter);
        assertThat(iutFound).as("Filter region: 1 iut found").hasSize(1);
        iut = iutFound.stream().findFirst().get();
        assertThat(iut.getId()).as("Filter region: IUT found is Laval").isEqualTo(ti.getIutLaval().getId());
        assertThat(iut.getDepartements()).as("Filter region: 1 dept is proposed: GB").hasSize(3)
                .map(DepartementSummary::getCode).containsExactlyInAnyOrder("Dept-Laval-GB", "Dept-Laval-MMI", "Dept-Laval-INFO");

        // but
        filter = IUTFormationFilter.createBuilder().withButs(List.of("MMI", "GB")).build();
        iutFound = this.testedRepo.streamSummariesByFilter(filter);
        assertThat(iutFound).as("Filter but: 2 iut found").hasSize(2);
        assertThat(iutFound).as("Filter but: Good parcours retrieved").allSatisfy((iutSummary) -> {
            if (iutSummary.getId().equals(ti.getIutLannion().getId())) {
                assertThat(iutSummary.getDepartements()).as("Filter but: Lannion iut has 1 depts").hasSize(1);
            } else if (iutSummary.getId().equals(ti.getIutLaval().getId())) {
                assertThat(iutSummary.getDepartements()).as("Filter but: Laval iut has 2 depts").hasSize(2);
            } else {
                fail("Filter but: Bad iut found: " + iutSummary.getNom());
            }
        });

        // but + geo
        filter = IUTFormationFilter.createBuilder().withGPSFilter(
                ti.getIutLaval().getLocation().getY(),
                ti.getIutLaval().getLocation().getX(),
                50D)
                .withButs(List.of("MMI")).build();
        iutFound = this.testedRepo.streamSummariesByFilter(filter);
        assertThat(iutFound).as("Filter BUT+geo: 1 iut found").hasSize(1);
        iut = iutFound.stream().findFirst().get();
        assertThat(iut.getId()).as("Filter BUT+geo: IUT found is Laval").isEqualTo(ti.getIutLaval().getId());
        assertThat(iut.getDepartements()).as("Filter BUT+geo: 1 dept is proposed: MMI").hasSize(1)
                .map(DepartementSummary::getCode).allMatch(c -> c.equals("Dept-Laval-MMI"));

        // free query + region
        filter = IUTFormationFilter.createBuilder()
                .withFreeTextQuery("référencement internet")
                .withRegions(List.of("BRETAGNE"))
                .build();
        iutFound = this.testedRepo.streamSummariesByFilter(filter);
        assertThat(iutFound).as("Filter Free text+region: 1 iut found").hasSize(1);
        iut = iutFound.stream().findFirst().get();
        assertThat(iut.getId()).as("Filter Free text+region: IUT found is Lannion").isEqualTo(ti.getIutLannion().getId());
        assertThat(iut.getDepartements()).as("Filter Free text+region: 1 dept is proposed: MMI").hasSize(1)
                .map(DepartementSummary::getCode).allMatch(c -> c.equals("Dept-Lannion-MMI"));

        // include all dept + but
        filter = IUTFormationFilter.createBuilder()
                .withButs(List.of("GB")).withIncludeAllDepts(true).build();
        iutFound = this.testedRepo.streamSummariesByFilter(filter);
        assertThat(iutFound).as("Filter dept+but: 1 iut found").hasSize(1);
        iut = iutFound.stream().findFirst().get();
        assertThat(iut.getId()).as("Filter dept+but: IUT found is Laval").isEqualTo(ti.getIutLaval().getId());
        assertThat(iut.getDepartements()).as("Filter dept+but: 1 dept is proposed: GB").hasSize(3)
                .map(DepartementSummary::getCode).containsExactlyInAnyOrder("Dept-Laval-GB", "Dept-Laval-MMI", "Dept-Laval-INFO");

    }
}
