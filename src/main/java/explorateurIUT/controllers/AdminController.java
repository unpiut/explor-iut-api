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

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import explorateurIUT.excelImport.AppDataProperties;

/**
 *
 * @author Remi Venant
 */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private static final Log LOG = LogFactory.getLog(AdminController.class);

    private final AppDataProperties appDataProperties = new AppDataProperties();

    @GetMapping("data-sheets")
    public ResponseEntity<?> getData(@AuthenticationPrincipal UserDetails currentUser) {
        try (FileInputStream fis = new FileInputStream(new File(this.appDataProperties.getFilePath()))) {
            ContentDisposition contentDisposition = ContentDisposition.builder("attachement") // doit être téléchargé
                    .filename("data.xlsx") // le nom du fichier
                    .build();
            HttpHeaders contentHeaders = new HttpHeaders();
            contentHeaders.setContentDisposition(contentDisposition);

            return ResponseEntity.ok() // ok() : code 200
                    .contentType(MediaType.valueOf("application/vnd.ms-excel")) // en-tête content-type positioné sur
                                                                                // "application/vnd.ms-excel » : un
                                                                                // fichier excel
                    .headers(contentHeaders)
                    .body(fis);
        }
    }

    @PutMapping("data-sheets")
    public String updateData(@AuthenticationPrincipal UserDetails currentUser) {
        return "T.B.A";
    }
}
