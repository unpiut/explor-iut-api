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
package explorateurIUT.services;

import explorateurIUT.services.mailManagement.MailSendingRequest;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author Remi Venant
 */
@ActiveProfiles({"development", "app-test", "mongo-test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MailManagementServiceTest {

    @Autowired
    private MailManagementService testedSvc;

    public MailManagementServiceTest() {
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

    /**
     * Test of requestMailSending method, of class MailManagementServiceImpl.
     */
    @Test
    public void testRequestMailSendingValidation() {
        URI baseUri = URI.create("http://localhost:8080");
        assertThatThrownBy(()
                -> this.testedSvc.requestMailSending(new MailSendingRequest(null, "Jean Bon", "Entreprise", "PDG", "jean.bon@mail.com", "le sujet", "le corps", null), baseUri))
                .as("Null iutIds rejected")
                .isInstanceOf(ConstraintViolationException.class);

        assertThatThrownBy(()
                -> this.testedSvc.requestMailSending(new MailSendingRequest(null, "Jean Bon", "Entreprise", "PDG", "jean.bon@mail.com", "le sujet", "le corps", null), baseUri))
                .as("Empty iutIds rejected")
                .isInstanceOf(ConstraintViolationException.class);

        final ArrayList<String> listWithNull = new ArrayList<>();
        listWithNull.add("0000000000-0000000000-00");
        listWithNull.add(null);
        assertThatThrownBy(()
                -> this.testedSvc.requestMailSending(new MailSendingRequest(listWithNull, "Jean Bon", "Entreprise", "PDG", "jean.bon@mail.com", "le sujet", "le corps", null), baseUri))
                .as("Null iutId in iutIds rejected")
                .isInstanceOf(ConstraintViolationException.class);

        assertThatThrownBy(()
                -> this.testedSvc.requestMailSending(new MailSendingRequest(List.of("0000000000-0000000000-00", "0000000000-0000000000-g0"), "Jean Bon", "Entreprise", "PDG", "jean.bon@mail.com", "le sujet", "le corps", null), baseUri))
                .as("invalid iutId in iutIds rejected")
                .isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(()
                -> this.testedSvc.requestMailSending(new MailSendingRequest(List.of("0000000000-0000000000-00", "0000000000-0000000000-0"), "Jean Bon", "Entreprise", "PDG", "jean.bon@mail.com", "le sujet", "le corps", null), baseUri))
                .as("invalid (bis) iutId in iutIds rejected")
                .isInstanceOf(ConstraintViolationException.class);
    }

}
