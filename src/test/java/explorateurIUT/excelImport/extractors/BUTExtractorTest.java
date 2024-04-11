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
package explorateurIUT.excelImport.extractors;

import explorateurIUT.excelImport.model.ExcelBUT;
import explorateurIUT.excelImport.model.ExcelParcoursBUT;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.poi.UnsupportedFileFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author Remi Venant
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles({"test", "mongo-test"})
public class BUTExtractorTest {

    @Value("classpath:data_sample.xlsx")
    private File dataSample;

    private String sampleButSheetName = "BUT";

    public BUTExtractorTest() {
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
     * Test of extractEntities method, of class BUTExtractor.
     */
    @Test
    public void testExtractEntities() throws IOException, UnsupportedFileFormatException {
        final ArrayList<ExcelBUT> buts = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(this.dataSample)) {
            final XSSFWorkbook wb = new XSSFWorkbook(fis);
            final BUTExtractor butExtractor = new BUTExtractor();
            butExtractor.extractEntities(wb.getSheet(this.sampleButSheetName), (excelBut) -> {
                buts.add(excelBut);
            });
        }
        assertThat(buts).as("6 buts extracted").hasSize(6);
        assertThat(buts).allSatisfy((b) -> this.validateExtractBut(b));
    }

    public void validateExtractBut(ExcelBUT but) {
        assertThat(but).as("But has not blank basic values")
                .extracting("code", "nom", "filiere", "description", "metiers", "urlFiche", "urlFranceCompetence", "universMetiers")
                .allMatch(v -> v instanceof String sv && !sv.isBlank());
        switch (but.getCode()) {
            case "GB" ->
                assertThat(but.getParcours()).as(but.getCode() + " has valid parcours")
                        .map(ExcelParcoursBUT::getCode).containsExactlyInAnyOrder("AGRO", "BMB", "DN", "SAB", "SEE");
            case "GCGP" ->
                assertThat(but.getParcours()).as(but.getCode() + " has valid parcours")
                        .map(ExcelParcoursBUT::getCode).containsExactlyInAnyOrder("CPIT", "CQESP", "CPOP");
            case "GCCD" ->
                assertThat(but.getParcours()).as(but.getCode() + " has valid parcours")
                        .map(ExcelParcoursBUT::getCode).containsExactlyInAnyOrder("BEC", "RAPEB", "BAT", "TP");
            case "INFO" ->
                assertThat(but.getParcours()).as(but.getCode() + " has valid parcours")
                        .map(ExcelParcoursBUT::getCode).containsExactlyInAnyOrder("RACDV", "DACS", "AGED", "IAMSI");
            case "GACO" ->
                assertThat(but.getParcours()).as(but.getCode() + " has valid parcours")
                        .map(ExcelParcoursBUT::getCode).containsExactlyInAnyOrder("MCMO", "MACAST", "MFS", "MRPE");
            case "Info-Com" ->
                assertThat(but.getParcours()).as(but.getCode() + " has valid parcours")
                        .map(ExcelParcoursBUT::getCode).containsExactlyInAnyOrder("CO", "INO", "JOURN", "MLP", "PUB");
        }
    }

    @TestConfiguration
    public class MyTestConf {

    }
}
