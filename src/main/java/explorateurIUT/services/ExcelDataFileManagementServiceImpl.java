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

import com.fasterxml.jackson.annotation.JsonIgnore;
import explorateurIUT.excelImport.AppDataProperties;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 *
 * @author Rémi Venant
 */
@Service
@Validated
public class ExcelDataFileManagementServiceImpl implements ExcelDataFileManagementService {

    public final static Set<String> MANAGED_FILE_EXTENSIONS = Set.of("xls", "xlsx");

    private final static DateTimeFormatter ISO_DATETIME_CONDENSED = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final static String ISO_DATETIME_CONDENSED_RE_PATTERN = "\\d{14}";

    private static final Log LOG = LogFactory.getLog(ExcelDataFileManagementServiceImpl.class);

    private final AppDataProperties appDataProperties;

    @Autowired
    public ExcelDataFileManagementServiceImpl(AppDataProperties appDataProperties) {
        this.appDataProperties = appDataProperties;
    }

    @PostConstruct
    public void init() throws IOException, IllegalStateException {
        // We list all files in the data dir that start with the prefix and whom extensions is handled
        // if none of them: throw exception
        // if several without proper pattern: throw exception
        // If only without proper pattern, rename it with the current datetime

        // For each: 
        final Pattern ldtPattern = this.createLDTPattern();
        try (Stream<Path> stream = Files.list(Paths.get(this.appDataProperties.getDataDir()))) {
            DataFilesStat fileStats = stream.filter(f -> !Files.isDirectory(f) && Files.isReadable(f)) // should be a regular readable file
                    .filter(f -> f.getFileName().toString().startsWith(this.appDataProperties.getDataFilePrefix())) // should start with a prefix
                    .filter(f -> MANAGED_FILE_EXTENSIONS.contains(ExcelDataFileManagementServiceImpl.extractFileExtensionLower(f))) // should end with a proper extension
                    .collect(DataFilesStat::new,
                            (dfs, path) -> dfs.addFile(path, this.extractLdtFromFilename(path, ldtPattern) != null),
                            (dfs1, dfs2) -> dfs1.accumulate(dfs2));

            if (fileStats.getNbFiles() == 0) {
                throw new IllegalStateException("No data file found");
            }
            if (fileStats.getNbUnpatterned() > 1) {
                throw new IllegalStateException("Too many data file without history marker");
            }
            // Rename the unpatterned file with the current datetime
            if (fileStats.nbUnpatterned == 1) {
                Path newPath = this.createFilePath(LocalDateTime.now(), extractFileExtensionLower(fileStats.getUnpatternedFilePath()));
                LOG.info("Single data file without time marker: rename it with the current time");
                Files.move(fileStats.getUnpatternedFilePath(), newPath);
            }
        }
    }

    @Override
    public List<DataFileHistoryEntryImpl> getHistory() throws IOException {
        final Pattern ldtPattern = this.createLDTPattern();
        try (Stream<Path> stream = Files.list(Paths.get(this.appDataProperties.getDataDir()))) {
            List<DataFileHistoryEntryImpl> entries = stream.filter(f -> !Files.isDirectory(f) && Files.isReadable(f)) // should be a regular readable file
                    .filter(f -> f.getFileName().toString().startsWith(this.appDataProperties.getDataFilePrefix())) // should start with a prefix
                    .filter(f -> MANAGED_FILE_EXTENSIONS.contains(ExcelDataFileManagementServiceImpl.extractFileExtensionLower(f))) // should end with a proper extension
                    .map((path) -> {
                        final LocalDateTime ldt = this.extractLdtFromFilename(path, ldtPattern);
                        if (ldt == null) {
                            return null;
                        }
                        return new DataFileHistoryEntryImpl(ldt, false, path);
                    })
                    .sorted()
                    .toList();
            // if entries not empty, set the first one as used
            if (!entries.isEmpty()) {
                entries.getFirst().setUsed(true);
            }
            return entries;
        }
    }

    @Override
    public Path getCurrentFilePath() throws IOException, NoSuchElementException {
        final List<DataFileHistoryEntryImpl> history = this.getHistory();
        return history.stream().filter(DataFileHistoryEntryImpl::isUsed)
                .findFirst()
                .map(DataFileHistoryEntryImpl::getPath)
                .orElseThrow(() -> new NoSuchElementException("No current data file found"));
    }

    @Override
    public Path getFilePath(LocalDateTime version) throws NoSuchElementException, IOException {
        final List<DataFileHistoryEntryImpl> history = this.getHistory();
        return history.stream().filter(e -> e.getVersion().equals(version))
                .findFirst()
                .map(DataFileHistoryEntryImpl::getPath)
                .orElseThrow(() -> new NoSuchElementException("No data file found for the given version"));
    }

    @Override
    public ExcelChangeSession getChangeExcelSession() throws IOException {
        return new ChangeSessionImpl();
    }

    private Path createFilePath(LocalDateTime ldt, String extension) throws IllegalArgumentException {
        final String lExt = extension.toLowerCase();
        if (!MANAGED_FILE_EXTENSIONS.contains(lExt)) {
            throw new IllegalArgumentException("Bad extension " + extension);
        }
        String fullFileName = this.appDataProperties.getDataFilePrefix() + "_" + ldt.format(ISO_DATETIME_CONDENSED) + "." + lExt;
        return Path.of(this.appDataProperties.getDataDir()).resolve(fullFileName);
    }

    private Pattern createLDTPattern() {
        return Pattern.compile("^" + this.appDataProperties.getDataFilePrefix() + "_(" + ISO_DATETIME_CONDENSED_RE_PATTERN + ")\\..*", Pattern.CASE_INSENSITIVE);
    }

    private LocalDateTime extractLdtFromFilename(Path filePath, Pattern ldtPattern) {
        final String filename = filePath.getFileName().toString();
        final Matcher m = ldtPattern.matcher(filename);
        if (!m.matches()) {
            return null;
        }
        String ldtCandidate = m.group(1);
        try {
            return LocalDateTime.parse(ldtCandidate, ISO_DATETIME_CONDENSED);
        } catch (DateTimeParseException ex) {
            return null;
        }

    }

    private static String extractFileExtensionLower(Path filePath) {
        final String filename = filePath.getFileName().toString();
        int dotPos = filename.lastIndexOf(".");
        if (dotPos < 0) {
            return "";
        }
        return filename.substring(dotPos + 1).toLowerCase();
    }

    public static class DataFileHistoryEntryImpl implements DataFileHistoryEntry, Comparable<DataFileHistoryEntry> {

        private final LocalDateTime version;

        private boolean used;

        @JsonIgnore
        private final Path path;

        public DataFileHistoryEntryImpl(LocalDateTime version, boolean used, Path path) {

            this.version = version;
            this.used = used;
            this.path = path;
        }

        @Override
        public LocalDateTime getVersion() {
            return this.version;
        }

        public void setUsed(boolean used) {
            this.used = used;
        }

        @Override
        public boolean isUsed() {
            return this.used;
        }

        public Path getPath() {
            return this.path;
        }

        @Override
        public int compareTo(DataFileHistoryEntry o) {
            return o.getVersion().compareTo(this.version);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + Objects.hashCode(this.version);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final DataFileHistoryEntryImpl other = (DataFileHistoryEntryImpl) obj;
            return Objects.equals(this.version, other.version);
        }

        @Override
        public String toString() {
            return "DataFileHistoryEntryImpl{" + "version=" + version + ", used=" + used + ", path=" + path + '}';
        }

    }

    public class ChangeSessionImpl implements ExcelChangeSession {

        private Path newPath;
        private boolean fileChanged = false;

        @Override
        public void applyChange(InputStream dataExcel) throws IOException, SecurityException {
            // Create a file path with the current datetime with the xlsx extension
            this.newPath = createFilePath(LocalDateTime.now(), DEFAULT_FILE_EXTENSION);
            LOG.info("Create a new data file at " + this.newPath.toString());
            Files.copy(dataExcel, this.newPath, StandardCopyOption.REPLACE_EXISTING);
            this.fileChanged = true;
        }

        @Override
        public void commit() {
            // do nothing
        }

        @Override
        public void rollback() throws IOException, SecurityException {
            if (this.fileChanged) {
                LOG.info("Remove data file at " + this.newPath.toString());
                Files.deleteIfExists(this.newPath);
            }
        }

    }

    private static class DataFilesStat {

        private int nbFiles;
        private int nbUnpatterned;
        private Path unpatternedFilePath;

        public DataFilesStat() {
            this.nbFiles = 0;
            this.nbUnpatterned = 0;
            this.unpatternedFilePath = null;
        }

        public DataFilesStat(int nbFiles, int nbUnpatterned, Path unpatternedFilePath) {
            this.nbFiles = nbFiles;
            this.nbUnpatterned = nbUnpatterned;
            this.unpatternedFilePath = unpatternedFilePath;
        }

        public int getNbFiles() {
            return nbFiles;
        }

        public int getNbUnpatterned() {
            return nbUnpatterned;
        }

        public Path getUnpatternedFilePath() {
            return unpatternedFilePath;
        }

        public DataFilesStat addPatternedFile(Path p) {
            return this.addFile(p, true);
        }

        public DataFilesStat addUnPatternedFile(Path p) {
            return this.addFile(p, false);
        }

        public DataFilesStat addFile(Path p, boolean patterned) {
            this.nbFiles++;
            if (!patterned) {
                this.nbUnpatterned++;
                this.unpatternedFilePath = p;
            }
            return this;
        }

        public DataFilesStat accumulate(DataFilesStat dfs) {
            this.nbFiles += dfs.nbFiles;
            this.nbUnpatterned += dfs.nbUnpatterned;
            if (this.unpatternedFilePath == null) {
                this.unpatternedFilePath = dfs.unpatternedFilePath;
            }
            return this;
        }

    }

}
