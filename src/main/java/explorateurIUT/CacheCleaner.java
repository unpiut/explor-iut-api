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
package explorateurIUT;

import explorateurIUT.services.CacheManagementService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 *
 * @author Remi Venant
 */
@Profile("clear-caches")
@Component
public class CacheCleaner implements CommandLineRunner {

    private static final Log LOG = LogFactory.getLog(CacheCleaner.class);

    private final CacheManagementService cacheMgmtSvc;

    @Autowired
    public CacheCleaner(CacheManagementService cacheMgmtSvc) {
        this.cacheMgmtSvc = cacheMgmtSvc;
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("Clear cache");
        this.cacheMgmtSvc.resetCaches();
        final String etag = this.cacheMgmtSvc.setAndGetCacheEtag();
        LOG.info("Set new etag cache: " + etag);
    }
}
