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

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.ValidationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 *
 * @author Remi Venant
 */
@Service
@Validated
public class MailSendingServiceImpl implements MailSendingService {

    private static final Log LOG = LogFactory.getLog(MailSendingServiceImpl.class);

    private final JavaMailSender mailSender;

    private final MailSendingProperties mailSendingProperties;

    @Autowired
    public MailSendingServiceImpl(JavaMailSender mailSender, MailSendingProperties mailSendingProperties) {
        this.mailSender = mailSender;
        this.mailSendingProperties = mailSendingProperties;
    }

    @Override
    public void sendMail(Collection<String> recipients, String replyTo, String subject, String body, List<GridFsResource> attachements) throws ValidationException, MailException, MessagingException {
        try {
            String[] recipientsArray;;
            if (this.mailSendingProperties.getTestingMailAddress() != null) {
                recipientsArray = new String[]{this.mailSendingProperties.getTestingMailAddress()};
            } else {
                recipientsArray = new String[recipients.size()];
                recipientsArray = recipients.toArray(recipientsArray);
            }

            final MimeMessage message = this.createRawMessage(recipientsArray, replyTo, subject, body, attachements);
            this.mailSender.send(message);
        } catch (MessagingException ex) {
            LOG.error("Unable to create student absence mail.", ex);
            throw ex;
        } catch (MailException ex) {
            LOG.error("Unable to send  student absence mail.", ex);
            throw ex;
        }
    }

    private MimeMessage createRawMessage(String[] recipients, String replyTo, String subject,
            String body, List<GridFsResource> attachements) throws MessagingException {
        final MimeMessage message = this.mailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(message);

        switch (this.mailSendingProperties.getSendingType().toLowerCase()) {
            case "to" -> {
                helper.setTo(recipients);
            }
            case "cc" -> {
                helper.setCc(recipients);
            }
            case "bcc" -> {
                helper.setBcc(recipients);
            }
            default -> {
                LOG.warn("Invalid sending type \"" + this.mailSendingProperties.getSendingType() + "\". Revert to bcc");
                helper.setBcc(recipients);
            }
        }
        LOG.debug("We have set the recipients: " + Arrays.toString(recipients));

        helper.setFrom(this.mailSendingProperties.getFromAddress());
        helper.setReplyTo(replyTo);
        helper.setSubject(subject);
        helper.setText(body, false); // set text as plain text

        if (attachements != null && !attachements.isEmpty()) {
            for (GridFsResource attachement : attachements) {
                try {
                    InputStreamSource iss = new InputStreamResource(attachement.getInputStream());
                    helper.addAttachment(attachement.getFilename(), iss, attachement.getContentType());
                } catch (IOException ex) {
                    LOG.error("Cannot attach resource to mail", ex);
                }
            }
        }
        return message;
    }

}
