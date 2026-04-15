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
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author rvenant
 */
public class JPAQuotaValidator implements GlobalQuotaValidator, IPQuotaValidator {

    private static final Log LOG = LogFactory.getLog(JPAQuotaValidator.class);

    private final EntityManager entityManager;
    private int maxRequestPerMinute = 100;
    private int maxIpRequestPerHour = 10;
    private int maxIpRequestPerDeptPerHour = 5;

    // An atomic reference to the global counter to allow concurrent access
    private final AtomicReference<GlobalCounter> globalCounter;

    public JPAQuotaValidator(EntityManager entityManager) {
        this.entityManager = entityManager;
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
    @Transactional
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
        this.entityManager.persist(ipQuota);
    }

    @Override
    @Transactional
    public void cleanOutdatedQuotas() {
        LOG.info("Clean outdate quotas");
        /*
        Remove all IPQuota documents whose all counters (global and depts) are outdated
        
        -- QUERY 1
        delete from IPDepartementQuota ipdq
        where ipdq.ipQuota in (
            select ipq from IPQuota ipq 
            where ipq.started < MINGLOBALTIME
            and not exists (
                select 1 from IPDepartementQuota ipdq
                where ipdq.ipQuota = ipq
                and ipdq.started >= MINGLOBALTIME
            )
        )
        
        -- QUERY 2
        delete from IPQuota ipq 
        where ipq.started < MINGLOBALTIME
        and not exists (
            select 1 from IPDepartementQuota ipdq
            where ipdq.ipQuota = ipq
            and ipdq.started >= MINGLOBALTIME
        )
         */
        final LocalDateTime minDt = LocalDateTime.now().minusMinutes(1);
        final CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();

        // QUERY 1
        // 1.1. Root Delete operation on IPDepartementQuota
        CriteriaDelete<IPDepartementQuota> deleteIPDQ = cb.createCriteriaDelete(IPDepartementQuota.class);
        Root<IPDepartementQuota> rootIPDQ = deleteIPDQ.from(IPDepartementQuota.class);
        // 1.2. The outer Subquery: select ipq from IPQuota ipq ...
        Subquery<IPQuota> outerSubquery = deleteIPDQ.subquery(IPQuota.class);
        Root<IPQuota> rootIPQ = outerSubquery.from(IPQuota.class);
        // 1.3. The inner Subquery: select 1 from IPDepartementQuota where ...
        Subquery<Integer> innerSubquery = outerSubquery.subquery(Integer.class);
        Root<IPDepartementQuota> rootIPDQInner = innerSubquery.from(IPDepartementQuota.class);
        innerSubquery.select(cb.literal(1))
                .where(
                        cb.equal(rootIPDQInner.get("ipQuota"), rootIPQ),
                        cb.greaterThanOrEqualTo(rootIPDQInner.get("started"), minDt)
                );
        // 1.4. Combine conditions for the outer Subquery
        outerSubquery.select(rootIPQ)
                .where(
                        cb.lessThan(rootIPQ.get("started"), minDt),
                        cb.not(cb.exists(innerSubquery))
                );
        // 1.5. Apply the final WHERE clause to the Delete statement
        deleteIPDQ.where(rootIPDQ.get("ipQuota").in(outerSubquery));
        // 1.6. Execute
        int nbRowsDelete = entityManager.createQuery(deleteIPDQ).executeUpdate();
        LOG.info(String.format("- %d IPDepartementQuota deleted", nbRowsDelete));

        // QUERY 2
        // 2.1. Root Delete operation on IPQuota
        CriteriaDelete<IPQuota> deleteIPQ = cb.createCriteriaDelete(IPQuota.class);
        rootIPQ = deleteIPQ.from(IPQuota.class);
        // 2.2.  Create the Subquery for the "NOT EXISTS" clause
        Subquery<Integer> subquery = deleteIPQ.subquery(Integer.class);
        rootIPDQ = subquery.from(IPDepartementQuota.class);

        // SELECT 1 FROM IPDepartementQuota ipdq WHERE ipdq.ipQuota = ipq AND ipdq.started >= MINGLOBALTIME
        subquery.select(cb.literal(1))
                .where(
                        cb.equal(rootIPDQ.get("ipQuota"), rootIPQ),
                        cb.greaterThanOrEqualTo(rootIPQ.get("started"), minDt)
                );

        // 2.3. Assemble the top-level WHERE clause
        // WHERE ipq.started < MINGLOBALTIME AND NOT EXISTS (...)
        deleteIPQ.where(
                cb.lessThan(rootIPQ.get("started"), minDt),
                cb.not(cb.exists(subquery))
        );
        // 4. Execute the update
        nbRowsDelete = entityManager.createQuery(deleteIPQ).executeUpdate();
        LOG.info(String.format("- %d IPQuota deleted", nbRowsDelete));
    }

    private static record GlobalCounter(LocalDateTime creation, int count) {

    }

    private IPQuota findByIP(String ip) {
        /*
         SELECT IPQuota ipq WHERE ipq.ip = ip LIMIT 1 
         */
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        // 1. Create the Query
        CriteriaQuery<IPQuota> query = cb.createQuery(IPQuota.class);
        Root<IPQuota> ipq = query.from(IPQuota.class);
        // 2. Add the WHERE clause: WHERE ipq.ip = :myIP
        query.select(ipq)
                .where(cb.equal(ipq.get("ip"), ip));
        // 3. Apply the LIMIT 1 on the TypedQuery instance
        List<IPQuota> results = entityManager.createQuery(query)
                .setMaxResults(1)
                .getResultList();
        // 4. Return the result safely
        return results.isEmpty() ? null : results.get(0);
    }
}
