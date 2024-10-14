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

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import java.io.IOException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author Remi Venant
 */
@ActiveProfiles({"development", "app-test", "mail-test", "mongo-test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MailSendingServiceImplTest {

    private static final Log LOG = LogFactory.getLog(MailSendingServiceImplTest.class);

    private static SimpleSmtpServer server;

    @Autowired
    private JavaMailSender javaMailSender;

    public MailSendingServiceImplTest() {
    }

    @BeforeAll
    public static void setUpClass() throws IOException {
        server = SimpleSmtpServer.start(3333);
    }

    @AfterAll
    public static void tearDownClass() {
        server.stop();
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
        server.reset();
    }

    /**
     * Test of sendMailToIUT method, of class MailSendingServiceImpl.
     */
    @Test
    public void testSendMail() throws Exception {
        MailSendingProperties msp = new MailSendingProperties();
        msp.setFromAddress("from@mail.com");

        MailSendingServiceImpl mailSendingSvc = new MailSendingServiceImpl(javaMailSender, msp, null);

        String replyTo = "reply@mail.com";
        String subject = "The subject";
        String body = "The body";
        List<String> recipients = List.of("recipient1@mail.com", "recipient2@mail.com");

        LOG.info("Send mail");
        for (String recipient : recipients) {
            mailSendingSvc.sendMailToIUT(recipient, replyTo, subject, body, List.of());
        }
        LOG.info("Check if mail received");
        assertThat(server.getReceivedEmails()).hasSize(2);
        LOG.info("Assess mail info");
        SmtpMessage messageRecevied = server.getReceivedEmails().get(0);
        assertThat(messageRecevied.getHeaderValue("Reply-To")).isEqualTo(replyTo);
        assertThat(messageRecevied.getHeaderValue("From")).isEqualTo(msp.getFromAddress());
        assertThat(messageRecevied.getHeaderValue("To")).isIn(recipients);
        final String recipientMail1 = messageRecevied.getHeaderValue("To");
        assertThat(messageRecevied.getHeaderValue("Cc")).isNull();
        assertThat(messageRecevied.getHeaderValue("Subject")).isEqualTo(subject);
        assertThat(messageRecevied.getBody()).contains(body);

        messageRecevied = server.getReceivedEmails().get(1);
        assertThat(messageRecevied.getHeaderValue("Reply-To")).isEqualTo(replyTo);
        assertThat(messageRecevied.getHeaderValue("From")).isEqualTo(msp.getFromAddress());
        assertThat(messageRecevied.getHeaderValue("To")).isIn(recipients).isNotEqualTo(recipientMail1);
        assertThat(messageRecevied.getHeaderValue("Cc")).isNull();
        assertThat(messageRecevied.getHeaderValue("Subject")).isEqualTo(subject);
        assertThat(messageRecevied.getBody()).contains(body);
    }

    @Test
    public void testSendMailWithTestingMailAddr() throws Exception {
        MailSendingProperties msp = new MailSendingProperties();
        msp.setFromAddress("from@mail.com");
        msp.setTestingMailAddress("test@mail.com");
        MailSendingServiceImpl mailSendingSvc = new MailSendingServiceImpl(javaMailSender, msp, null);

        String replyTo = "reply@mail.com";
        String subject = "The subject";
        String body = "The body";
        List<String> recipients = List.of("recipient1@mail.com", "recipient2@mail.com");

        LOG.info("Send mail");
        for (String recipient : recipients) {
            mailSendingSvc.sendMailToIUT(recipient, replyTo, subject, body, List.of());
        }
        LOG.info("Check if mail received");
        assertThat(server.getReceivedEmails()).hasSize(2);
        LOG.info("Assess mail info");
        SmtpMessage messageRecevied = server.getReceivedEmails().get(0);
        assertThat(messageRecevied.getHeaderValue("Reply-To")).isEqualTo(replyTo);
        assertThat(messageRecevied.getHeaderValue("From")).isEqualTo(msp.getFromAddress());
        assertThat(messageRecevied.getHeaderValue("To")).isIn(recipients);
        final String recipientMail1 = messageRecevied.getHeaderValue("To");
        assertThat(messageRecevied.getHeaderValue("Cc")).isNull();
        assertThat(messageRecevied.getHeaderValue("Subject")).isEqualTo(subject);
        assertThat(messageRecevied.getBody()).contains(body);

        messageRecevied = server.getReceivedEmails().get(1);
        assertThat(messageRecevied.getHeaderValue("Reply-To")).isEqualTo(replyTo);
        assertThat(messageRecevied.getHeaderValue("From")).isEqualTo(msp.getFromAddress());
        assertThat(messageRecevied.getHeaderValue("To")).isIn(recipients).isNotEqualTo(recipientMail1);
        assertThat(messageRecevied.getHeaderValue("Cc")).isNull();
        assertThat(messageRecevied.getHeaderValue("Subject")).isEqualTo(subject);
        assertThat(messageRecevied.getBody()).contains(body);
    }

}
