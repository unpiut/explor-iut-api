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

import explorateurIUT.excelImport.AppDataProperties;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;

/**
 *
 * @author Rémi Venant
 */
public class ExcelDataFileManagementServiceImplTest {

    private AppDataProperties appDataProperties;

    private ExcelDataFileManagementServiceImpl testedSvc;

    public ExcelDataFileManagementServiceImplTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        this.appDataProperties = new AppDataProperties();
        this.appDataProperties.setDataDir("/a-dumb-path");
        this.appDataProperties.setDataFilePrefix("data");
        this.testedSvc = new ExcelDataFileManagementServiceImpl(appDataProperties);
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of init method, of class ExcelDataFileManagementServiceImpl.
     */
    @Test
    public void testInitRenameOneFile() throws Exception {
        // Mock Files iteration to generate 2 files with timestamp, 1 file without
        try (MockedStatic<Files> fileMock = Mockito.mockStatic(Files.class)) {
            // Prepare Mock
            BDDMockito.given(Files.list(Paths.get(this.appDataProperties.getDataDir())))
                    .willReturn(Stream.of(
                            Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + "_20221201000000.xlsx"),
                            Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + ".xlsx"),
                            Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + "_20231201000000.xlsx")));
            BDDMockito.given(Files.move(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).willAnswer((invoc) -> invoc.getArgument(1));
            BDDMockito.given(Files.isDirectory(ArgumentMatchers.any())).willReturn(false);
            BDDMockito.given(Files.isReadable(ArgumentMatchers.any())).willReturn(true);

            //Call init
            this.testedSvc.init();

            //Check methods calls on init
            fileMock.verify(() -> Files.list(Paths.get(this.appDataProperties.getDataDir())));
            fileMock.verify(() -> Files.isDirectory(ArgumentMatchers.any()), times(3));
            fileMock.verify(() -> Files.isReadable(ArgumentMatchers.any()), times(3));
            fileMock.verify(() -> Files.move(
                    ArgumentMatchers.eq(Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + ".xlsx")),
                    ArgumentMatchers.any()),
                    times(1));
        }
    }

    @Test
    public void testInitNoRename() throws Exception {
        // Mock Files iteration to generate 2 files with timestamp, 1 file without
        try (MockedStatic<Files> fileMock = Mockito.mockStatic(Files.class)) {
            // Prepare Mock
            BDDMockito.given(Files.list(Paths.get(this.appDataProperties.getDataDir())))
                    .willReturn(Stream.of(
                            Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + "_20221201000000.xlsx"),
                            Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + "_20230201000000.xlsx"),
                            Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + "_20231201000000.xlsx")));
            BDDMockito.given(Files.move(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).willAnswer((invoc) -> invoc.getArgument(1));
            BDDMockito.given(Files.isDirectory(ArgumentMatchers.any())).willReturn(false);
            BDDMockito.given(Files.isReadable(ArgumentMatchers.any())).willReturn(true);

            //Call init
            this.testedSvc.init();

            //Check methods calls on init
            fileMock.verify(() -> Files.list(Paths.get(this.appDataProperties.getDataDir())));
            fileMock.verify(() -> Files.isDirectory(ArgumentMatchers.any()), times(3));
            fileMock.verify(() -> Files.isReadable(ArgumentMatchers.any()), times(3));
            fileMock.verify(() -> Files.move(
                    ArgumentMatchers.any(),
                    ArgumentMatchers.any()),
                    times(0));
        }
    }

    @Test
    public void testInitFailOnNoFile() throws Exception {
        // Mock Files iteration to generate 2 files with timestamp, 1 file without
        try (MockedStatic<Files> fileMock = Mockito.mockStatic(Files.class)) {
            // Prepare Mock
            BDDMockito.given(Files.list(Paths.get(this.appDataProperties.getDataDir())))
                    .willReturn(Stream.of(
                            Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + "_20221201000000"),
                            Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + "_20230201000000.xlsx"),
                            Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + "_20231201000000.badex")));
            BDDMockito.given(Files.move(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).willAnswer((invoc) -> invoc.getArgument(1));
            BDDMockito.given(Files.isDirectory(ArgumentMatchers.any())).willReturn(true, false);
            BDDMockito.given(Files.isReadable(ArgumentMatchers.any())).willReturn(false, true);

            //Call init
            assertThatThrownBy(() -> {
                this.testedSvc.init();
            }).isInstanceOf(IllegalStateException.class);

            //Check methods calls on init
            fileMock.verify(() -> Files.list(Paths.get(this.appDataProperties.getDataDir())));
            fileMock.verify(() -> Files.isDirectory(ArgumentMatchers.any()), times(3));
            fileMock.verify(() -> Files.isReadable(ArgumentMatchers.any()), times(2));
            fileMock.verify(() -> Files.move(
                    ArgumentMatchers.any(),
                    ArgumentMatchers.any()),
                    times(0));
        }
    }

    @Test
    public void testInitFailTooManyNoLdtFile() throws Exception {
        // Mock Files iteration to generate 2 files with timestamp, 1 file without
        try (MockedStatic<Files> fileMock = Mockito.mockStatic(Files.class)) {
            // Prepare Mock
            BDDMockito.given(Files.list(Paths.get(this.appDataProperties.getDataDir())))
                    .willReturn(Stream.of(
                            Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + "_20221201000000.xlsx"),
                            Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + ".xlsx"),
                            Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + "2.xlsx")));
            BDDMockito.given(Files.move(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).willAnswer((invoc) -> invoc.getArgument(1));
            BDDMockito.given(Files.isDirectory(ArgumentMatchers.any())).willReturn(false);
            BDDMockito.given(Files.isReadable(ArgumentMatchers.any())).willReturn(true);

            //Call init
            assertThatThrownBy(() -> {
                this.testedSvc.init();
            }).isInstanceOf(IllegalStateException.class);

            //Check methods calls on init
            fileMock.verify(() -> Files.list(Paths.get(this.appDataProperties.getDataDir())));
            fileMock.verify(() -> Files.isDirectory(ArgumentMatchers.any()), times(3));
            fileMock.verify(() -> Files.isReadable(ArgumentMatchers.any()), times(3));
            fileMock.verify(() -> Files.move(
                    ArgumentMatchers.any(),
                    ArgumentMatchers.any()),
                    times(0));
        }
    }

    /**
     * Test of getHistory method, of class ExcelDataFileManagementServiceImpl.
     */
    @Test
    public void testGetHistory() throws Exception {
        final Path[] sourcesFiles = new Path[]{
            Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + "_20221201030201.xlsx"),
            Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + "_20231201010203.xlsx"),
            Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + "_20230201010203.xlsx")
        };
        // Mock Files iteration to generate 2 files with timestamp, 1 file without
        try (MockedStatic<Files> fileMock = Mockito.mockStatic(Files.class)) {
            // Prepare Mock
            BDDMockito.given(Files.list(Paths.get(this.appDataProperties.getDataDir())))
                    .willReturn(Stream.of(sourcesFiles));
            BDDMockito.given(Files.isDirectory(ArgumentMatchers.any())).willReturn(false);
            BDDMockito.given(Files.isReadable(ArgumentMatchers.any())).willReturn(true);

            //get history
            List<ExcelDataFileManagementServiceImpl.DataFileHistoryEntryImpl> history = this.testedSvc.getHistory();

            //Check methods calls on init
            fileMock.verify(() -> Files.list(Paths.get(this.appDataProperties.getDataDir())));
            fileMock.verify(() -> Files.isDirectory(ArgumentMatchers.any()), times(3));
            fileMock.verify(() -> Files.isReadable(ArgumentMatchers.any()), times(3));

            assertThat(history).as("3 entries returned").hasSize(3);
            assertThat(history.get(0)).as("First entry most recent and proper attrs")
                    .extracting("version", "used", "path")
                    .containsExactly(LocalDateTime.of(2023, Month.DECEMBER, 1, 1, 2, 3), true, sourcesFiles[1]);
            assertThat(history.get(1)).as("Second entry most proper attrs")
                    .extracting("version", "used", "path")
                    .containsExactly(LocalDateTime.of(2023, Month.FEBRUARY, 1, 1, 2, 3), false, sourcesFiles[2]);
            assertThat(history.get(2)).as("First entry last recent and proper attrs")
                    .extracting("version", "used", "path")
                    .containsExactly(LocalDateTime.of(2022, Month.DECEMBER, 1, 3, 2, 1), false, sourcesFiles[0]);
        }
    }

    /**
     * Test of getCurrentFilePath method, of class
     * ExcelDataFileManagementServiceImpl.
     */
    @Test
    public void testGetCurrentFilePath() throws Exception {
        final Path[] sourcesFiles = new Path[]{
            Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + "_20221201030201.xlsx"),
            Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + "_20231201010203.xlsx"),
            Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + "_20230201010203.xlsx")
        };
        // Mock Files iteration to generate 2 files with timestamp, 1 file without
        try (MockedStatic<Files> fileMock = Mockito.mockStatic(Files.class)) {
            // Prepare Mock
            BDDMockito.given(Files.list(Paths.get(this.appDataProperties.getDataDir())))
                    .willReturn(Stream.of(sourcesFiles));
            BDDMockito.given(Files.isDirectory(ArgumentMatchers.any())).willReturn(false);
            BDDMockito.given(Files.isReadable(ArgumentMatchers.any())).willReturn(true);

            //get history
            Path currentFilePath = this.testedSvc.getCurrentFilePath();

            //Check methods calls on init
            fileMock.verify(() -> Files.list(Paths.get(this.appDataProperties.getDataDir())));
            fileMock.verify(() -> Files.isDirectory(ArgumentMatchers.any()), times(3));
            fileMock.verify(() -> Files.isReadable(ArgumentMatchers.any()), times(3));

            assertThat(currentFilePath).as("Proper path returned").isEqualTo(sourcesFiles[1]);
        }
    }

    /**
     * Test of getFilePath method, of class ExcelDataFileManagementServiceImpl.
     */
    @Test
    public void testGetFilePath() throws Exception {
        final Path[] sourcesFiles = new Path[]{
            Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + "_20221201030201.xlsx"),
            Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + "_20231201010203.xlsx"),
            Path.of(this.appDataProperties.getDataDir(), this.appDataProperties.getDataFilePrefix() + "_20230201010203.xlsx")
        };
        // Mock Files iteration to generate 2 files with timestamp, 1 file without
        try (MockedStatic<Files> fileMock = Mockito.mockStatic(Files.class)) {
            // Prepare Mock
            BDDMockito.given(Files.list(Paths.get(this.appDataProperties.getDataDir())))
                    .willAnswer((invoc) -> Stream.of(sourcesFiles));
            BDDMockito.given(Files.isDirectory(ArgumentMatchers.any())).willReturn(false);
            BDDMockito.given(Files.isReadable(ArgumentMatchers.any())).willReturn(true);

            //get history
            Path aPath = this.testedSvc.getFilePath(LocalDateTime.of(2022, Month.DECEMBER, 1, 3, 2, 1));

            //Check methods calls on init
            fileMock.verify(() -> Files.list(Paths.get(this.appDataProperties.getDataDir())));
            fileMock.verify(() -> Files.isDirectory(ArgumentMatchers.any()), times(3));
            fileMock.verify(() -> Files.isReadable(ArgumentMatchers.any()), times(3));

            assertThat(aPath).as("Proper path returned").isEqualTo(sourcesFiles[0]);

            //Call init
            assertThatThrownBy(() -> {
                this.testedSvc.getFilePath(LocalDateTime.of(2022, Month.NOVEMBER, 1, 3, 2, 1));
            }).isInstanceOf(NoSuchElementException.class);
        }
    }

    /**
     * Test of getChangeExcelSession method, of class
     * ExcelDataFileManagementServiceImpl.
     */
    @Test
    public void testGetChangeExcelSessionCommit() throws Exception {
        // Mock Files iteration to generate 2 files with timestamp, 1 file without
        try (MockedStatic<Files> fileMock = Mockito.mockStatic(Files.class)) {
            BDDMockito.given(Files.copy(ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(Path.class), ArgumentMatchers.any()))
                    .willAnswer((invoc) -> invoc.getArgument(0, InputStream.class).skip(Long.MAX_VALUE));

            BDDMockito.given(Files.deleteIfExists(ArgumentMatchers.any()))
                    .willReturn(false);

            ExcelDataFileManagementService.ExcelChangeSession ecs = this.testedSvc.getChangeExcelSession();
            ByteArrayInputStream bais = new ByteArrayInputStream(new byte[200]);
            ecs.applyChange(bais);
            ecs.commit();

            fileMock.verify(() -> Files.copy(ArgumentMatchers.eq(bais),
                    ArgumentMatchers.assertArg((Path p) -> {
                        assertThat(p.toString()).as("Destination file path is in data dir").startsWith(this.appDataProperties.getDataDir());
                        assertThat(p.getFileName().toString()).as("Destination file name starts with data prefix and ends with extension")
                                .startsWith(this.appDataProperties.getDataFilePrefix())
                                .endsWith("." + ExcelDataFileManagementService.DEFAULT_FILE_EXTENSION.toLowerCase());
                    }),
                    ArgumentMatchers.eq(StandardCopyOption.REPLACE_EXISTING)));

            fileMock.verify(() -> Files.deleteIfExists(ArgumentMatchers.any()), times(0));
        }
    }

    @Test
    public void testGetChangeExcelSessionRollback() throws Exception {
        // Mock Files iteration to generate 2 files with timestamp, 1 file without
        try (MockedStatic<Files> fileMock = Mockito.mockStatic(Files.class)) {
            BDDMockito.given(Files.copy(ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(Path.class), ArgumentMatchers.any()))
                    .willAnswer((invoc) -> invoc.getArgument(0, InputStream.class).skip(Long.MAX_VALUE));

            BDDMockito.given(Files.deleteIfExists(ArgumentMatchers.any()))
                    .willReturn(false);

            ExcelDataFileManagementService.ExcelChangeSession ecs = this.testedSvc.getChangeExcelSession();
            ByteArrayInputStream bais = new ByteArrayInputStream(new byte[200]);
            ecs.applyChange(bais);
            ecs.rollback();

            fileMock.verify(() -> Files.copy(ArgumentMatchers.eq(bais),
                    ArgumentMatchers.assertArg((Path p) -> {
                        assertThat(p.toString()).as("Destination file path is in data dir").startsWith(this.appDataProperties.getDataDir());
                        assertThat(p.getFileName().toString()).as("Destination file name starts with data prefix and ends with extension")
                                .startsWith(this.appDataProperties.getDataFilePrefix())
                                .endsWith("." + ExcelDataFileManagementService.DEFAULT_FILE_EXTENSION.toLowerCase());
                    }),
                    ArgumentMatchers.eq(StandardCopyOption.REPLACE_EXISTING)));

            fileMock.verify(() -> Files.deleteIfExists(ArgumentMatchers.any()), times(1));
        }
    }

}
