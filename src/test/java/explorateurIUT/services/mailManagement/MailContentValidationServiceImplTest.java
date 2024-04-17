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
public class MailContentValidationServiceImplTest {

    private MailSendingProperties mailSendingProp;
    private MailContentValidationServiceImpl mailContentValidationServiceImpl;

    public MailContentValidationServiceImplTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        this.mailSendingProp = new MailSendingProperties();
        this.mailContentValidationServiceImpl = new MailContentValidationServiceImpl(this.mailSendingProp);
        this.mailContentValidationServiceImpl.init();
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of isValid method, of class MailContentValidationServiceImpl.
     */
    @Test
    public void testIsValid() {
        String context = "A clean content";
        assertThat(this.mailContentValidationServiceImpl.isValid(context)).as("Clean content is valid").isTrue();
        context = "A html inject <a href=\"click here\">content</a>";
        assertThat(this.mailContentValidationServiceImpl.isValid(context)).as("Html content is invalid").isFalse();
    }

}
