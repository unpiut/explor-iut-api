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
package explorateurIUT.security.mailQuota.services;

import explorateurIUT.security.mailQuota.model.IPDepartementQuota;
import explorateurIUT.security.mailQuota.model.IPQuota;
import jakarta.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 *
 * @author rvenant
 */
public class MongoQuotaValidator implements GlobalQuotaValidator, IPQuotaValidator {

    private static final Log LOG = LogFactory.getLog(MongoQuotaValidator.class);

    private final MongoTemplate mongoTemplate;
    private int maxRequestPerMinute = 100;
    private int maxIpRequestPerHour = 10;
    private int maxIpRequestPerDeptPerHour = 5;

    // An atomic reference to the global counter to allow concurrent access
    private final AtomicReference<GlobalCounter> globalCounter;

    public MongoQuotaValidator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.globalCounter = new AtomicReference<>(new GlobalCounter(LocalDateTime.MIN, 0));
    }

    public int getMaxRequestPerMinute() {
        return maxRequestPerMinute;
    }

    public void setMaxRequestPerMinute(int maxRequestPerMinute) {
        this.maxRequestPerMinute = maxRequestPerMinute;
    }

    public int getMaxIpRequestPerHour() {
        return maxIpRequestPerHour;
    }

    public void setMaxIpRequestPerHour(int maxIpRequestPerHour) {
        this.maxIpRequestPerHour = maxIpRequestPerHour;
    }

    public int getMaxIpRequestPerDeptPerHour() {
        return maxIpRequestPerDeptPerHour;
    }

    public void setMaxIpRequestPerDeptPerHour(int maxIpRequestPerDeptPerHour) {
        this.maxIpRequestPerDeptPerHour = maxIpRequestPerDeptPerHour;
    }

    @Override
    public boolean validateAndUpdateRequestCounter() {
        LOG.debug("Validate and update request counter");
        // Get current datetime at minute precision
        final LocalDateTime now = LocalDateTime.now();
        // Update and get the counter
        final GlobalCounter counter = this.globalCounter.updateAndGet((gc) -> {
            if (gc.creation().isBefore(now.minusMinutes(1))) {
                // previous counter was created more than 1 minute ago: replace it with a new counter initialied to 1
                return new GlobalCounter(now, 1);
            } else if (gc.count() <= this.maxRequestPerMinute) {
                // limit has not been exceeded yet: increment it
                return new GlobalCounter(gc.creation(), gc.count() + 1);
            } else {
                // limit has already been exceeded within the last minute: unchange the counter
                return gc;
            }
        });
        // In the limit has been exceeded, throw an exception
        return counter.count() <= this.maxRequestPerMinute;
    }

    @Override
    public boolean validateIPRequest(String clientIP, Collection<String> deptIds) throws ValidationException {
        LOG.debug("Validate IP request with clientIP " + clientIP + " and deptIds: " + (deptIds == null ? "null" : deptIds.toString()));
        // Retrieve the IPQuota is it exists
        final IPQuota ipQuota = this.findByIP(clientIP);
        if (ipQuota == null) {
            // No saved ipQuota: request is valid
            return true;
        }
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime minDt = now.minusHours(1);
        // Validate against global counter
        if (!ipQuota.getStarted().isBefore(minDt) && ipQuota.getCounter() >= this.maxIpRequestPerHour) {
            LOG.info("Validation of request failed on IP request per minute limit");
            return false;
        }
        // If no deptIds, request is valid
        if (deptIds == null || deptIds.isEmpty()) {
            return true;
        }
        // Create a set of deptId whose limit has alrady been reached
        final Set<String> limitedDeptIds = ipQuota.getDepartementQuotas().stream()
                // Filter over counter still valid in time and which has already reached its limit
                .filter(idq -> idq.getStarted().isAfter(minDt) && idq.getCounter() >= this.maxIpRequestPerDeptPerHour)
                .map(IPDepartementQuota::getDeptId)
                .collect(Collectors.toSet());
        // Verify that no deptIds have reached limit
        if (deptIds.stream().anyMatch(limitedDeptIds::contains)) {
            LOG.info("Validation of request failed on IP request per dept per hour limit");
            return false;
        }
        return true;
    }

    @Override
    public void updateIPRequestCounter(String clientIP, Collection<String> deptIds) throws ValidationException {
        LOG.debug("Update IP request with clientIP " + clientIP + " and deptIds: " + (deptIds == null ? "null" : deptIds.toString()));
        // Retrieve the IPQuota is it exists
        IPQuota ipQuota = this.findByIP(clientIP);
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime limitDt = now.minusHours(1);
        // create ipQuota with initial update counteur or update its counter
        if (ipQuota == null) {
            ipQuota = new IPQuota(clientIP, now, 1);
        } else {
            ipQuota.incrementCounter(1, limitDt, now);
        }
        if (deptIds != null && !deptIds.isEmpty()) {
            for (String deptId : deptIds) {
                ipQuota.updateOrCreateDepartementQuota(deptId, 1, limitDt, now);
            }
        }
        // Save the updated quota
        this.mongoTemplate.save(ipQuota);
    }

    @Override
    public void cleanOutdatedQuotas() {
        /*
        Remove all IPQuot mongo documents whose all counters (global and depts) are outdated
        Raw query is { started: {$lt: MINGLOBALTIME}, $not: { departementQuotas: { $elemMatch: {started: { $gte: MINDEPTTIME } } } } }
         */
        final LocalDateTime minDt = LocalDateTime.now().minusMinutes(1);
        Criteria crit = Criteria.where("started").lt(minDt).and("departementQuotas").not().elemMatch(Criteria.where("started").gte(minDt));
        Query query = Query.query(crit);
        this.mongoTemplate.remove(query, IPQuota.class);

        // Do nothing for the global quota, no cleaning needed
    }

    private static record GlobalCounter(LocalDateTime creation, int count) {

    }

    private IPQuota findByIP(String ip) {
        Query query = Query.query(Criteria.where("ip").is(ip));
        return this.mongoTemplate.findOne(query, IPQuota.class);
    }
}
