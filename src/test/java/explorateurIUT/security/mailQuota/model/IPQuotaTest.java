/*
 * Copyright (C) 2026 IUT Laval - Le Mans Université.
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
package explorateurIUT.security.mailQuota.model;

import explorateurIUT.configuration.JPAConfiguration;
import explorateurIUT.model.PendingMailRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author Rémi Venant
 */
@Import({JPAConfiguration.class})
@DataJpaTest
@ActiveProfiles({"test", "db-hsqldb"})
public class IPQuotaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PendingMailRepository pendingMailRepo;

    public IPQuotaTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testCreateIPQuota() {
        System.out.println("persist IP Quota");
        IPQuota ipq = new IPQuota("129.168.1.1", LocalDateTime.now(), 4);
        ipq.updateOrCreateDepartementQuota("deptId1", 3, LocalDateTime.MIN, LocalDateTime.now());

        assertThat(ipq.getId()).as("IPq unsaved does not have id").isNull();
        ipq = this.entityManager.persistAndFlush(ipq);
        this.entityManager.clear();

        assertThat(ipq.getId()).as("Pm saved has an id").isNotNull();
        QuotaCount quotaCount = this.countIPQuota();
        assertThat(quotaCount.ipQuota()).as("1 ip quota in db").isEqualTo(1);
        assertThat(quotaCount.ipDeptQuota()).as("1 ip dept quota in db").isEqualTo(1);

        IPQuota retrievedIpq = this.entityManager.find(IPQuota.class, ipq.getId());
        retrievedIpq.updateOrCreateDepartementQuota("deptId2", 2, LocalDateTime.MIN, LocalDateTime.now());
        retrievedIpq.updateOrCreateDepartementQuota("deptId3", 3, LocalDateTime.MIN, LocalDateTime.now());
        this.entityManager.persistAndFlush(retrievedIpq);
        this.entityManager.clear();

        quotaCount = this.countIPQuota();
        assertThat(quotaCount.ipQuota()).as("After dept update: 1 ip quota in db").isEqualTo(1);
        assertThat(quotaCount.ipDeptQuota()).as("After dept update: 3 ip dept quota in db").isEqualTo(3);

        retrievedIpq = this.entityManager.find(IPQuota.class, ipq.getId());
        this.entityManager.remove(retrievedIpq);
        this.entityManager.flush();
        this.entityManager.clear();

        quotaCount = this.countIPQuota();
        assertThat(quotaCount.ipQuota()).as("After remove: 0 ip quota in db").isEqualTo(0);
        assertThat(quotaCount.ipDeptQuota()).as("After remove: 0 ip dept quota in db").isEqualTo(0);
    }

    private static record QuotaCount(Long ipQuota, Long ipDeptQuota) {

    }

    private QuotaCount countIPQuota() {
        CriteriaBuilder cb = this.entityManager.getEntityManager().getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<IPQuota> ipqRoot = countQuery.from(IPQuota.class);
        countQuery.select(cb.count(ipqRoot));
        Long nbIpQuota = this.entityManager.getEntityManager().createQuery(countQuery).getSingleResult();

        countQuery = cb.createQuery(Long.class);
        Root<IPDepartementQuota> ipdqRoot = countQuery.from(IPDepartementQuota.class);
        countQuery.select(cb.count(ipdqRoot));
        Long nbIpdQuota = this.entityManager.getEntityManager().createQuery(countQuery).getSingleResult();

        return new QuotaCount(nbIpQuota, nbIpdQuota);
    }

    /**
     * Test of incrementCounter method, of class IPQuota.
     */
    @Test
    public void testIncrementCounter() {
        System.out.println("incrementCounter");
        LocalDateTime now = LocalDateTime.now();
        IPQuota ipq = new IPQuota("129.168.1.1", now, 4);

        assertThat(ipq.getCounter()).as("Initial counter is 4").isEqualTo(4);

        // Increment with minimum time after started dt will increment without changing coutner dt
        ipq.incrementCounter(3, now.minusSeconds(1), now.plusSeconds(3));
        assertThat(ipq.getCounter()).as("counter has been incremented to 7").isEqualTo(7);
        assertThat(ipq.getStarted()).as("started dt is still the initial value").isEqualTo(now);

        // Increment with minimum time before started dt will set counter and change started dt
        ipq.incrementCounter(3, now.plusSeconds(1), now.plusMinutes(3));
        assertThat(ipq.getCounter()).as("counter has been set to 3").isEqualTo(3);
        assertThat(ipq.getStarted()).as("started dt is now changed").isEqualTo(now.plusMinutes(3));
    }

    /**
     * Test of updateOrCreateDepartementQuota method, of class IPQuota.
     */
    @Test
    public void testUpdateOrCreateDepartementQuota() {
        System.out.println("updateOrCreateDepartementQuota");
        LocalDateTime now = LocalDateTime.now();
        IPQuota ipq = new IPQuota("129.168.1.1", now, 4);
        ipq.updateOrCreateDepartementQuota("deptId1", 3, now, now.plusMinutes(1));

        assertThat(ipq.getDepartementQuotas()).as("One initial dept quotas").hasSize(1);
        IPDepartementQuota ipdq = ipq.getDepartementQuotas().stream().findFirst().get();
        assertThat(ipdq.getCounter()).as("initial dept quota counter is 3").isEqualTo(3);

        ipdq = ipq.updateOrCreateDepartementQuota("deptId1", 4, now.plusMinutes(1), now.plusMinutes(3));
        assertThat(ipq.getDepartementQuotas()).as("ip dept quotas has still size 1").hasSize(1);
        assertThat(ipdq.getCounter()).as("initial dept quota counter has been incremented to 7").isEqualTo(7);
        assertThat(ipdq.getStarted()).as("initial dept quota started is sill now+1min").isEqualTo(now.plusMinutes(1));

        ipdq = ipq.updateOrCreateDepartementQuota("deptId1", 4, now.plusMinutes(2), now.plusMinutes(5));
        assertThat(ipq.getDepartementQuotas()).as("ip dept quotas has still size 1").hasSize(1);
        assertThat(ipdq.getCounter()).as("initial dept quota counter is now 4").isEqualTo(4);
        assertThat(ipdq.getStarted()).as("initial dept quota started is now now+3min").isEqualTo(now.plusMinutes(5));

        ipdq = ipq.updateOrCreateDepartementQuota("deptId2", 5, now.plusSeconds(1), now.plusMinutes(3));
        assertThat(ipq.getDepartementQuotas()).as("ip dept quotas has now size 2").hasSize(2);
    }

}
