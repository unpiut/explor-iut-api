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
import explorateurIUT.model.IUT;
import explorateurIUT.model.projections.IUTSummary;
import explorateurIUT.model.views.IUTViews;
import explorateurIUT.services.EtagAccessService;
import explorateurIUT.services.IUTService;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Remi Venant
 */
@RestController
@RequestMapping("/api/v1/iut")
public class IUTController {

    private static final Log LOG = LogFactory.getLog(IUTController.class);

    private final IUTService iutSvc;

    private final EtagAccessService etagAccessSvc;

    @Autowired
    public IUTController(IUTService iutSvc, EtagAccessService etagAccessSvc) {
        this.iutSvc = iutSvc;
        this.etagAccessSvc = etagAccessSvc;
    }

    @GetMapping
    public ResponseEntity<Stream<IUTSummary>> getIUTSummaries(@RequestParam MultiValueMap<String, String> params) {
        if (params == null || params.isEmpty()) {
            List<IUTSummary> iutSummaries = this.iutSvc.findIUTSummaries();
            return ResponseEntity
                    .ok()
                    .cacheControl(CacheControl.maxAge(this.etagAccessSvc.getCacheEtagDuration()))
                    .eTag(this.etagAccessSvc.getCacheEtag())
                    .body(iutSummaries.stream());
        }
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.noStore())
                .body(this.iutSvc.streamFilteredIUTSummaries(this.iutSvc.generateFilterFromQueryParams(params)));
    }

    @JsonView(IUTViews.Details.class)
    @GetMapping("{iutId:[abcdef0-9]{24}}")
    public ResponseEntity<IUT> getIUT(@PathVariable String iutId) {
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.noStore())
                .body(this.iutSvc.findIUT(iutId));
    }
}
