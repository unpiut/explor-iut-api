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
package explorateurIUT.model;

import explorateurIUT.configuration.JPAConfiguration;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
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
public class PendingMailRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PendingMailRepository pendingMailRepo;

    public PendingMailRepositoryTest() {
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
    public void testCreatePendingMail() {
        System.out.println("persist pending mail with attachement and recipient");
        PendingMail pm = new PendingMail("subject", "body", "replyTo", "contactName");
        List<String> dptCodes1 = List.of("code1", "code2");
        pm.getRecipients().add(new PendingMailIUTRecipient(pm, "recAddr1", dptCodes1));
        List<String> dptCodes2 = List.of("code2", "code3");
        pm.getRecipients().add(new PendingMailIUTRecipient(pm, "recAddr2", dptCodes2));
        byte[] data1 = "aaa".getBytes();
        pm.getAttachements().add(new PendingMailAttachement(pm, "att1", "attType1", data1));
        byte[] data2 = "aaa".getBytes();
        pm.getAttachements().add(new PendingMailAttachement(pm, "att2", "attType2", data2));

        assertThat(pm.getId()).as("Pm unsaved does not have id").isNull();
        assertThat(pm.getCreationDateTime()).as("Pm unsaved does not have creationDateTime").isNull();

        LocalDateTime now = LocalDateTime.now();
        pm = this.entityManager.persistAndFlush(pm);
        this.entityManager.clear();

        assertThat(pm.getId()).as("Pm saved has an id").isNotNull();
        assertThat(pm.getCreationDateTime()).as("Pm saved does have creationDateTime").isNotNull();
        assertThat(pm.getCreationDateTime()).as("Pm saved does have valid creationDateTime").isCloseTo(now, within(50, ChronoUnit.MILLIS));

        System.out.println("Count entities in db");
        MailCount counts = this.countPendinMails();
        assertThat(counts.pendingMails()).as("1 pending mail in db").isEqualTo(1);
        assertThat(counts.recipients()).as("2 recipients in db").isEqualTo(2);
        assertThat(counts.attachements()).as("2 attachement in db").isEqualTo(2);

        System.out.println("Retrieve saved pm");
        PendingMail retrievedPm = this.entityManager.find(PendingMail.class, pm.getId());
        assertThat(retrievedPm).as("Retrieved pm is not null").isNotNull();

        System.out.println("Retrieve saved recipients from pending mail");
        Set<PendingMailIUTRecipient> recipients = retrievedPm.getRecipients();
        assertThat(recipients).as("2 recipients").hasSize(2);
        for (PendingMailIUTRecipient recipient : recipients) {
            List<String> expectedCodes = recipient.getMailAddress().equals("recAddr1") ? dptCodes1 : dptCodes2;
            assertThat(recipient.getDepartementCodes()).as("recipient dept codes not null and valid")
                    .isNotNull().containsExactlyInAnyOrderElementsOf(expectedCodes);
        }

        System.out.println("Retrieve saved attachement from pending mail");
        Set<PendingMailAttachement> attachements = retrievedPm.getAttachements();
        assertThat(attachements).as("2 attachements").hasSize(2);
        for (PendingMailAttachement attachement : attachements) {
            byte[] expectedData = attachement.getFileName().equals("att1") ? data1 : data2;
            assertThat(attachement.getContent())
                    .as(String.format("Content of attachement %s valid", attachement.getFileName()))
                    .isEqualTo(expectedData);
        }

        System.out.println("Delete pm");
        this.entityManager.remove(retrievedPm);
        this.entityManager.flush();
        this.entityManager.clear();

        System.out.println("Check after deletion");
        counts = this.countPendinMails();
        assertThat(counts.pendingMails()).as("0 pending mail in db").isEqualTo(0);
        assertThat(counts.recipients()).as("0 recipients in db").isEqualTo(0);
        assertThat(counts.attachements()).as("0 attachement in db").isEqualTo(0);
    }

    private static record MailCount(Long pendingMails, Long recipients, Long attachements) {

    }

    private MailCount countPendinMails() {
        CriteriaBuilder cb = this.entityManager.getEntityManager().getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<PendingMail> pmRoot = countQuery.from(PendingMail.class);
        countQuery.select(cb.count(pmRoot));
        Long nbPm = this.entityManager.getEntityManager().createQuery(countQuery).getSingleResult();

        countQuery = cb.createQuery(Long.class);
        Root<PendingMailIUTRecipient> pmrRoot = countQuery.from(PendingMailIUTRecipient.class);
        countQuery.select(cb.count(pmrRoot));
        Long nbPmr = this.entityManager.getEntityManager().createQuery(countQuery).getSingleResult();

        countQuery = cb.createQuery(Long.class);
        Root<PendingMailAttachement> pmaRoot = countQuery.from(PendingMailAttachement.class);
        countQuery.select(cb.count(pmaRoot));
        Long nbPma = this.entityManager.getEntityManager().createQuery(countQuery).getSingleResult();

        return new MailCount(nbPm, nbPmr, nbPma);
    }

    /**
     * Test of findByCreationDateTimeAndReplyTo method, of class
     * PendingMailRepository.
     */
    @Test
    public void testFindByCreationDateTimeAndReplyTo() {
        System.out.println("findByCreationDateTimeAndReplyTo");

        PendingMail initialPM = new PendingMail("subject", "body", "replyTo", "contactName");
        initialPM.getRecipients().add(new PendingMailIUTRecipient(initialPM, "recAddr1", List.of("code1", "code2")));
        initialPM = this.entityManager.persistAndFlush(initialPM);
        this.entityManager.clear();

        System.out.println("findByCreationDateTimeAndReplyTo with matching values");
        Optional<PendingMail> optPm = this.pendingMailRepo.findByCreationDateTimeAndReplyTo(initialPM.getCreationDateTime(), initialPM.getReplyTo());
        assertThat(optPm).as("PM found by creationDtAndReplyTo").isPresent();

        System.out.println("findByCreationDateTimeAndReplyTo with unmatched replyTo");
        optPm = this.pendingMailRepo.findByCreationDateTimeAndReplyTo(initialPM.getCreationDateTime(), "replyTo2");
        assertThat(optPm).as("PM not found by creationDtAndReplyTo with bad replyTo").isNotPresent();

        System.out.println("findByCreationDateTimeAndReplyTo with unmatched replyTo");
        optPm = this.pendingMailRepo.findByCreationDateTimeAndReplyTo(initialPM.getCreationDateTime().minusSeconds(1), initialPM.getReplyTo());
        assertThat(optPm).as("PM not found by creationDtAndReplyTo with bad createDateTime").isNotPresent();
    }

    /**
     * Test of findAndSetLastConfirmationMailById method, of class
     * PendingMailRepository.
     */
    @Test
    public void testFindAndSetLastConfirmationMailById() {
        System.out.println("findAndSetLastConfirmationMailById");

        PendingMail initialPM = new PendingMail("subject", "body", "replyTo", "contactName");
        initialPM.getRecipients().add(new PendingMailIUTRecipient(initialPM, "recAddr1", List.of("code1", "code2")));
        initialPM = this.entityManager.persistAndFlush(initialPM);
        this.entityManager.clear();

        assertThat(initialPM.getLastConfirmationMail()).as("Initial last confirmation mail is null").isNull();

        LocalDateTime now = LocalDateTime.now();
        int res = this.pendingMailRepo.findAndSetLastConfirmationMailById(initialPM.getId(), now);
        assertThat(res).as("1 pm modified by findAndSetLastConfirmationMailById").isEqualTo(1);

        System.out.println("Retrieve pm to check lastConfirmationMail");
        PendingMail retrievedPM = this.entityManager.find(PendingMail.class, initialPM.getId());
        assertThat(retrievedPM.getLastConfirmationMail()).as("last confirmation mail is now not null").isEqualTo(now);
    }

    /**
     * Test of clearMailsByCreationDateTimeBefore method, of class
     * PendingMailRepository.
     */
    @Test
    public void testClearMailsByCreationDateTimeBefore() {
        System.out.println("clearMailsByCreationDateTimeBefore");
        PendingMail pm1 = this.entityManager.persist(this.createTestData());
        LocalDateTime timeBoundary = LocalDateTime.now();
        PendingMail pm2 = this.entityManager.persist(this.createTestData());

        MailCount counts = this.countPendinMails();
        assertThat(counts.pendingMails()).as("2 pending mail in db").isEqualTo(2);
        assertThat(counts.recipients()).as("4 recipients in db").isEqualTo(4);
        assertThat(counts.attachements()).as("4 attachements in db").isEqualTo(4);

        System.out.println("clearMailsByCreationDateTimeBefore timeBoundary");
        int res = this.pendingMailRepo.clearMailsByCreationDateTimeBefore(timeBoundary);
        assertThat(res).as("5 rows must have been deleted").isEqualTo(5);

        counts = this.countPendinMails();
        assertThat(counts.pendingMails()).as("1 pending mail in db").isEqualTo(1);
        assertThat(counts.recipients()).as("2 recipients in db").isEqualTo(2);
        assertThat(counts.attachements()).as("2 attachements in db").isEqualTo(2);

    }

    private PendingMail createTestData() {
        PendingMail pm = new PendingMail("subject", "body", "replyTo", "contactName");
        pm.getRecipients().add(new PendingMailIUTRecipient(pm, "recAddr1", List.of("code1", "code2")));
        pm.getRecipients().add(new PendingMailIUTRecipient(pm, "recAddr2", List.of("code2", "code3")));
        pm.getAttachements().add(new PendingMailAttachement(pm, "att1", "attType1", "aaa".getBytes()));
        pm.getAttachements().add(new PendingMailAttachement(pm, "att2", "attType2", "bbb".getBytes()));

        return pm;
    }

}
