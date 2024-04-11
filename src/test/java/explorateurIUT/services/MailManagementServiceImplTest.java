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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 *
 * @author Remi Venant
 */
public class MailManagementServiceImplTest {

    private MailManagementServiceImpl testedSvc;

    public MailManagementServiceImplTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        this.testedSvc = new MailManagementServiceImpl();
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of requestMailSending method, of class MailManagementServiceImpl.
     */
    @Test
    public void testRequestMailSending() {
        System.out.println("requestMailSending");
        fail("The test case is a prototype.");
    }

    /**
     * Test of resendConfirmationMail method, of class
     * MailManagementServiceImpl.
     */
    @Test
    public void testResendConfirmationMail() {
        System.out.println("resendConfirmationMail");
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeOutdatedPendingMailRequest method, of class
     * MailManagementServiceImpl.
     */
    @Test
    public void testRemoveOutdatedPendingMailRequest() {
        System.out.println("removeOutdatedPendingMailRequest");
        fail("The test case is a prototype.");
    }

    /**
     * Test of confirmMailSendingRequest method, of class
     * MailManagementServiceImpl.
     */
    @Test
    public void testConfirmMailSendingRequest() {
        System.out.println("confirmMailSendingRequest");
        fail("The test case is a prototype.");
    }

}
