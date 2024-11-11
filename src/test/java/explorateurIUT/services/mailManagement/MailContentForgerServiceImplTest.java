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
package explorateurIUT.services.mailManagement;

import explorateurIUT.configuration.MongoConfiguration;
import explorateurIUT.configuration.TestDatasetConfig;
import explorateurIUT.model.Departement;
import explorateurIUT.model.DepartementRepository;
import explorateurIUT.model.IUTRepository;
import explorateurIUT.model.MailIUTRecipient;
import explorateurIUT.model.TestDatasetGenerator;
import explorateurIUT.services.AppTextService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author Remi Venant
 */
@DataMongoTest
@Import({MongoConfiguration.class, TestDatasetConfig.class})
@ActiveProfiles({"test", "mongo-test"})
public class MailContentForgerServiceImplTest {

    private AutoCloseable mocks;

    private MailSendingProperties mailSendingProp;

    @Autowired
    private IUTRepository iutRepo;

    @Autowired
    private DepartementRepository deptRepo;

    @Autowired
    private TestDatasetGenerator testDataset;

    private final AppTextService appTextService = new DumbAppTextService();

    private MailContentForgerServiceImpl testedSvc;

    public MailContentForgerServiceImplTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        this.testDataset.createDataset();
        this.mailSendingProp = new MailSendingProperties();
        this.mailSendingProp.setTestingMailAddress("testaddr@mail.com");
        this.testedSvc = new MailContentForgerServiceImpl(deptRepo, iutRepo, mailSendingProp, appTextService);
    }

    @AfterEach
    public void tearDown() {
        this.testDataset.clear();
    }

    /**
     * Test of createGeneralBody method, of class MailContentForgerServiceImpl.
     */
    @Test
    public void testCreateGeneralBody() {
        System.out.println("createGeneralBody");
        final TestDatasetGenerator.TestInstances ti = this.testDataset.getTestInstances();
        List<String> allLavalDeptIds = ti.getIutLaval().getDepartements().stream().map(Departement::getId).toList();
        MailSendingRequest mailSendingRequest = new MailSendingRequest(allLavalDeptIds,
                "contactIdentity", "contactCompany", "contactFunction", "contactMail", "subject", "body", null);

        final String body = this.testedSvc.createGeneralBody(mailSendingRequest);
        assertThat(body).as("Le corps créé est vide.").isNotBlank();
    }

    /**
     * Test of createIUTMailingList method, of class
     * MailContentForgerServiceImpl.
     */
    @Test
    public void testCreateIUTMailingList() {
        System.out.println("createIUTMailingList");
        final TestDatasetGenerator.TestInstances ti = this.testDataset.getTestInstances();
        List<String> allLavalDeptIds = ti.getIutLaval().getDepartements().stream().map(Departement::getId).toList();
        MailSendingRequest mailSendingRequest = new MailSendingRequest(allLavalDeptIds,
                "contactIdentity", "contactCompany", "contactFunction", "contactMail", "subject", "body", null);

        List<MailIUTRecipient> mailContact = this.testedSvc.createIUTMailingList(mailSendingRequest);

        assertThat(mailContact).as("Only one mail contact: iut laval").hasSize(1);
        assertThat(mailContact.get(0).getMailAddress()).as("mail is iut laval").isEqualTo(ti.getIutLaval().getMel());

    }

    private static class DumbAppTextService implements AppTextService {

        @Override
        public Map<String, String> getAppTextsByCode(String language) {
            return Map.of();
        }

        @Override
        public Map<String, String> getDefaultAppTextsByCode() {
            return Map.of();
        }

        @Override
        public Map<String, String> getMailTextsByCode(String language) {
            return Map.of();
        }

        @Override
        public Map<String, String> getDefaultMailTextsByCode() {
            return Map.of();
        }

    }
}
