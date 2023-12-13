/*
 * Copyright (C) 2023 IUT Laval - Le Mans Université.
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

import com.fasterxml.jackson.annotation.JsonView;
import explorateurIUT.model.BUT;
import explorateurIUT.model.projections.BUTSummary;
import explorateurIUT.model.views.BUTViews;
import explorateurIUT.services.BUTService;
import explorateurIUT.services.EtagAccessService;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Remi Venant
 */
@RestController
@RequestMapping("/api/v1/referentiel/but")
public class BUTController {

    private static final Log LOG = LogFactory.getLog(BUTController.class);

    private final BUTService butSvc;

    private final EtagAccessService etagAccessSvc;

    @Autowired
    public BUTController(BUTService butSvc, EtagAccessService etagAccessSvc) {
        this.butSvc = butSvc;
        this.etagAccessSvc = etagAccessSvc;
    }

    @GetMapping
    public ResponseEntity<List<BUTSummary>> getBUTSummaries() {
        List<BUTSummary> butSummaries = this.butSvc.findBUTSummaries();
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(this.etagAccessSvc.getCacheEtagDuration()))
                .eTag(this.etagAccessSvc.getCacheEtag())
                .body(butSummaries);
    }

    @JsonView(BUTViews.Details.class)
    @GetMapping("{butId:[abcdef0-9]{24}}")
    public ResponseEntity<BUT> getBUTDetails(@PathVariable String butId) {
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.noStore())
                .body(this.butSvc.findBUT(butId));
    }
    
    @JsonView(BUTViews.Details.class)
    @GetMapping("by-code/{butCode:\\w{2,20}}")
    public ResponseEntity<BUT> getBUTDetailsByCode(@PathVariable String butCode) {
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.noStore())
                .body(this.butSvc.findBUTByCode(butCode));
    }

}
