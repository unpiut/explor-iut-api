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
package explorateurIUT.security.mailQuota.services;

import static org.assertj.core.api.Assertions.assertThat;
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
 * @author Rémi Venant
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles({"test", "db-hsqldb"})
public class JPAQuotaValidatorTest {

    @Autowired
    private JPAQuotaValidator jpaQuotaValidator;

    public JPAQuotaValidatorTest() {
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
     * Test of validateAndUpdateRequestCounter method, of class
     * JPAQuotaValidator.
     */
    @Test
    public void testValidateAndUpdateRequestCounter() {
        System.out.println("validateAndUpdateRequestCounter");
        boolean res;
        for (int i = 0; i < this.jpaQuotaValidator.getMaxRequestPerMinute(); i++) {
            res = this.jpaQuotaValidator.validateAndUpdateRequestCounter();
            assertThat(res).as("Request counter still valid, " + i).isTrue();
        }
        res = this.jpaQuotaValidator.validateAndUpdateRequestCounter();
        assertThat(res).as("Request counter is invalid").isFalse();
    }

    /**
     * Test of validateIPRequest method, of class JPAQuotaValidator.
     */
//    @Test
    public void testValidateIPRequest() {
        System.out.println("validateIPRequest");
    }

    /**
     * Test of updateIPRequestCounter method, of class JPAQuotaValidator.
     */
//    @Test
    public void testUpdateIPRequestCounter() {
        System.out.println("updateIPRequestCounter");
    }

    /**
     * Test of cleanOutdatedQuotas method, of class JPAQuotaValidator.
     */
//    @Test
    public void testCleanOutdatedQuotas() {
        System.out.println("cleanOutdatedQuotas");
    }

}
