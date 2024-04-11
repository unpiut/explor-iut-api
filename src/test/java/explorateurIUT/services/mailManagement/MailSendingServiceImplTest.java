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
import java.util.stream.Collectors;
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
     * Test of sendMail method, of class MailSendingServiceImpl.
     */
    @Test
    public void testSendMail() throws Exception {
        MailSendingProperties msp = new MailSendingProperties();
        msp.setFromAddress("from@mail.com");
        msp.setSendingType("cc"); // we cannot test bcc as it wont be shown

        MailSendingServiceImpl mailSendingSvc = new MailSendingServiceImpl(javaMailSender, msp);

        String replyTo = "reply@mail.com";
        String subject = "The subject";
        String body = "The body";
        List<String> recipients = List.of("recipient1@mail.com", "recipient2@mail.com");

        LOG.info("Send mail");
        mailSendingSvc.sendMail(recipients, replyTo, subject, body, null);
        LOG.info("Check if mail received");
        assertThat(server.getReceivedEmails()).hasSize(1);
        LOG.info("Assess mail info");
        SmtpMessage messageRecevied = server.getReceivedEmails().get(0);
        assertThat(messageRecevied.getHeaderValue("Reply-To")).isEqualTo(replyTo);
        assertThat(messageRecevied.getHeaderValue("From")).isEqualTo(msp.getFromAddress());
        assertThat(messageRecevied.getHeaderValue("To")).isNull();
        assertThat(messageRecevied.getHeaderValue("Cc")).isEqualTo(recipients.stream().collect(Collectors.joining(", ")));
        assertThat(messageRecevied.getHeaderValue("Subject")).isEqualTo(subject);
        assertThat(messageRecevied.getBody()).isEqualTo(body);
    }

    @Test
    public void testSendMailWithTestingMailAddr() throws Exception {
        MailSendingProperties msp = new MailSendingProperties();
        msp.setFromAddress("from@mail.com");
        msp.setSendingType("cc"); // we cannot test bcc as it wont be shown
        msp.setTestingMailAddress("test@mail.com");
        MailSendingServiceImpl mailSendingSvc = new MailSendingServiceImpl(javaMailSender, msp);

        String replyTo = "reply@mail.com";
        String subject = "The subject";
        String body = "The body";
        List<String> recipients = List.of("recipient1@mail.com", "recipient2@mail.com");

        LOG.info("Send mail");
        mailSendingSvc.sendMail(recipients, replyTo, subject, body, null);
        LOG.info("Check if mail received");
        assertThat(server.getReceivedEmails()).hasSize(1);
        LOG.info("Assess mail info");
        SmtpMessage messageRecevied = server.getReceivedEmails().get(0);
        assertThat(messageRecevied.getHeaderValue("Reply-To")).isEqualTo(replyTo);
        assertThat(messageRecevied.getHeaderValue("From")).isEqualTo(msp.getFromAddress());
        assertThat(messageRecevied.getHeaderValue("To")).isNull();
        assertThat(messageRecevied.getHeaderValue("Cc")).isEqualTo(msp.getTestingMailAddress());
        assertThat(messageRecevied.getHeaderValue("Subject")).isEqualTo(subject);
        assertThat(messageRecevied.getBody()).isEqualTo(body);
    }

}
