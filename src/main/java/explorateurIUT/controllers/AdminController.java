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
package explorateurIUT.controllers;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import explorateurIUT.excelImport.AppDataProperties;
import explorateurIUT.services.DataUploadService;
import explorateurIUT.services.ExcelDataFileManagementService;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import org.springframework.core.io.PathResource;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author Remi Venant
 */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private static final Log LOG = LogFactory.getLog(AdminController.class);

    private final static DateTimeFormatter ISO_DATETIME_Export = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");

    private final DataUploadService dataUploader;

    private final AppDataProperties appDataProperties;

    private final ExcelDataFileManagementService excelDataFileMgmtSvc;

    @Autowired
    public AdminController(DataUploadService dataUploader, AppDataProperties appDataProperties, ExcelDataFileManagementService excelDataFileMgmtSvc) {
        this.dataUploader = dataUploader;
        this.appDataProperties = appDataProperties;
        this.excelDataFileMgmtSvc = excelDataFileMgmtSvc;
    }

    @GetMapping("data-sheets")
    public List<? extends ExcelDataFileManagementService.DataFileHistoryEntry> getDataHistoryEntries() throws IOException {
        return this.excelDataFileMgmtSvc.getHistory();
    }

    @GetMapping("data-sheets/current")
    public ResponseEntity<PathResource> getCurrentDataSheet() throws IOException {
        // Récupère le chemin du fichier
        final Path currentDataPath = this.excelDataFileMgmtSvc.getCurrentFilePath();
        // Prépare les en-têtes
        HttpHeaders headers = this.prepareDataResponseHttpHeaders("current");

        return ResponseEntity.ok()
                .headers(headers)
                .body(new PathResource(currentDataPath));
    }

    @GetMapping("data-sheets/{version:[0-9\\-:T]{19}}") // "yyyy-MM-ddThh:mm:ss" -> 19 chars
    public ResponseEntity<PathResource> getVersionDataSheet(@PathVariable LocalDateTime version) throws IOException {
        //LocalDateTime version = LocalDateTime.parse(rawVersion, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LOG.info("attempt access ds for version " + Objects.toString(version));
        // Récupère le chemin du fichier
        final Path currentDataPath = this.excelDataFileMgmtSvc.getFilePath(version);
        // Prépare les en-têtes
        HttpHeaders headers = this.prepareDataResponseHttpHeaders(version.format(ISO_DATETIME_Export));

        return ResponseEntity.ok()
                .headers(headers)
                .body(new PathResource(currentDataPath));
    }

    @PutMapping("data-sheets")
    public void updateData(@RequestParam("file") MultipartFile file)
            throws IOException {
        LOG.debug("Initialize update");
        dataUploader.uploadData(file);
    }

    private HttpHeaders prepareDataResponseHttpHeaders(String fileVersion) {
        // Prépare le titre du fichier : exportiut_{prefix}_version
        final String filename = "exploriut_" + this.appDataProperties.getDataFilePrefix() + "_" + fileVersion + "." + ExcelDataFileManagementService.DEFAULT_FILE_EXTENSION;

        // Prépare les en-tête de réponse personnalisé : Content-disposition et Content-type
        // Content-Length sera automatiquement calculé par ResponseEntity
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.builder("attachement") // dit au navigateur que le fichier doit être téléchargé (et pas affiché dans une page)
                        .filename(filename) // précise le nom du fichier
                        .build());
        // en-tête content-type positioné sur "application/vnd.ms-excel » : un fichier excel
        headers.setContentType(MediaType.valueOf("application/vnd.ms-excel"));
        return headers;
    }
}
