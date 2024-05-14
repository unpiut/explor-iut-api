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
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.PathResource;

/**
 *
 * @author Remi Venant
 */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private static final Log LOG = LogFactory.getLog(AdminController.class);

    private final DataUploadService dataUploader;

    private final AppDataProperties appDataProperties;

    @Autowired
    public AdminController(DataUploadService dataUploader, AppDataProperties appDataProperties) {
        this.dataUploader = dataUploader;
        this.appDataProperties = appDataProperties;
    }

    @GetMapping("data-sheets")
    public ResponseEntity<PathResource> getData() {
// Récupère le chemin du fichier
        final Path filePath = Paths.get(this.appDataProperties.getFilePath());
// Prépare les en-tête de réponse personnalisé : Content-disposition et Content-type
// Content-Length sera automatiquement calculé par ResponseEntity
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.builder("attachement") // dit au navigateur que le fichier doit être téléchargé (et pas affiché dans une page)
                        .filename("exploriut_data.xlsx") // précise le nom du fichier
                        .build());
// en-tête content-type positioné sur "application/vnd.ms-excel » : un fichier excel
        headers.setContentType(MediaType.valueOf("application/vnd.ms-excel"));

        return ResponseEntity.ok()
                .headers(headers)
                .body(new PathResource(filePath));
    }

    @PutMapping("data-sheets")
    public void updateData(@RequestParam("file") MultipartFile file)
            throws IOException {
        LOG.debug("Initialize update");
        dataUploader.uploadData(file);
    }
}
