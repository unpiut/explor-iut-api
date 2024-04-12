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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public class MailSendingRequestTokenServiceImplTest {

    private static final Log LOG = LogFactory.getLog(MailSendingRequestTokenServiceImplTest.class);

    private MailSendingProperties mailSendingProp;
    private MailSendingRequestTokenServiceImpl testedSvc;

    public MailSendingRequestTokenServiceImplTest() {
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
        this.mailSendingProp.setTokenSecret("myTokenSecret");
        this.testedSvc = new MailSendingRequestTokenServiceImpl(this.mailSendingProp);
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of createValidationToken method, of class
     * MailSendingRequestTokenServiceImpl.
     */
    @Test
    public void testCreateThenValidateToken() {
        String testId = "1234567890-abcdef1234-12";
        String token = this.testedSvc.createValidationToken(testId);
        assertThat(token).as("Token is not null nor blank").isNotNull().isNotBlank();
        //assertThat(token.split("\\.")).as("Token has only three points").hasSize(3);
        LOG.info("Token: " + token);

        String decodedId = this.testedSvc.decodeToken(token);
        assertThat(decodedId).as("DecodedId equals original id").isEqualTo(testId);
    }

    /**
     * Test of decodeToken method, of class MailSendingRequestTokenServiceImpl.
     */
    @Test
    public void testDecodeTokenEx() {
        assertThatThrownBy(()
                -> this.testedSvc.decodeToken("aa.bb"))
                .as("Bad token components rejected")
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(()
                -> this.testedSvc.decodeToken("aa.bb.cc"))
                .as("Bad token validity rejected")
                .isInstanceOf(IllegalArgumentException.class);
    }

}
