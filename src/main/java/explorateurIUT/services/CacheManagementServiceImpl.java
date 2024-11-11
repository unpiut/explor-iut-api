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
package explorateurIUT.services;

import java.time.Duration;
import static java.time.temporal.ChronoUnit.HOURS;
import java.util.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

/**
 *
 * @author Remi Venant
 */
@Service
public class CacheManagementServiceImpl implements CacheManagementService {

    private static final Log LOG = LogFactory.getLog(CacheManagementServiceImpl.class);

    @Caching(evict = {
        @CacheEvict(cacheNames = "butSummaries", allEntries = true, beforeInvocation = false),
        @CacheEvict(cacheNames = "iutSummaries", allEntries = true, beforeInvocation = false),
        @CacheEvict(cacheNames = "textSummaries", allEntries = true, beforeInvocation = false),
        @CacheEvict(cacheNames = "cacheEtag", allEntries = true, beforeInvocation = false)
    })
    @Override
    public void resetCaches() {
        LOG.info("Reset all caches...");
    }

    @CachePut(cacheNames = "cacheEtag", unless = "#result == null")
    @Override
    public String setAndGetCacheEtag() {
        return UUID.randomUUID().toString();
    }

    @Cacheable(cacheNames = "cacheEtag", unless = "#result == null")
    @Override
    public String getCacheEtag() {
        return null;
    }

    @Override
    public Duration getCacheEtagDuration() {
        return Duration.of(1, HOURS);
    }

}
