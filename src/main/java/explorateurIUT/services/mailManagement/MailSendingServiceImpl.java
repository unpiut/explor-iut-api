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

import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.ValidationException;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
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

    private final GridFsOperations gfsOperations;

    @Autowired
    public MailSendingServiceImpl(JavaMailSender mailSender, MailSendingProperties mailSendingProperties, GridFsOperations gfsOperations) {
        this.mailSender = mailSender;
        this.mailSendingProperties = mailSendingProperties;
        this.gfsOperations = gfsOperations;
    }

    @Override
    public void sendMailToIUT(Collection<String> recipients, String replyTo, String subject, String body, Stream<GridFSFile> attachements) throws ValidationException, MailException, MessagingException {
        try {
            String[] recipientsArray;;
            if (this.mailSendingProperties.getTestingMailAddress() != null) {
                recipientsArray = new String[]{this.mailSendingProperties.getTestingMailAddress()};
            } else {
                recipientsArray = new String[recipients.size()];
                recipientsArray = recipients.toArray(recipientsArray);
            }

            final MimeMessage message = this.createRawMessageForIUT(recipientsArray, replyTo, subject, body, attachements);
            this.mailSender.send(message);
        } catch (MessagingException ex) {
            LOG.error("Unable to create mail for IUT.", ex);
            throw ex;
        } catch (MailException ex) {
            LOG.error("Unable to send mail for IUT.", ex);
            throw ex;
        }
    }

    @Override
    public void sendMailToContact(String recipient, String subject, String body) throws ValidationException, MailException, MessagingException {
        try {
            final MimeMessage message = this.createRawMessageForContact(recipient, subject, body);
            this.mailSender.send(message);
        } catch (MessagingException ex) {
            LOG.error("Unable to create mail for contact.", ex);
            throw ex;
        } catch (MailException ex) {
            LOG.error("Unable to send mail for contact.", ex);
            throw ex;
        }
    }

    private MimeMessage createRawMessageForIUT(String[] recipients, String replyTo, String subject,
            String body, Stream<GridFSFile> attachements) throws MessagingException {
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

        helper.setFrom(this.mailSendingProperties.getFromAddress());
        helper.setReplyTo(replyTo);
        helper.setSubject(subject);
        helper.setText(body, false); // set text as plain text

        Optional<MessagingException> optEx = attachements.filter(f -> f != null).map(gridFSFile -> {
            try {
                final GridFsResource rsc = this.gfsOperations.getResource(gridFSFile);
                InputStreamSource iss = new InputStreamResource(rsc.getInputStream());
                helper.addAttachment(rsc.getFilename(), iss, rsc.getContentType());
                return null;
            } catch (IOException ex) {
                LOG.error("Cannot read resource to attach to mail", ex);
                return new MessagingException("Cannot read resource to attach to mail", ex);
            } catch (MessagingException ex) {
                LOG.error("Cannot read resource to attach to mail", ex);
                return ex;
            }
        }).filter(Objects::nonNull).findAny();
        if (optEx.isPresent()) {
            throw optEx.get();
        }
        return message;
    }

    private MimeMessage createRawMessageForContact(String recipient, String subject, String body) throws MessagingException {
        final MimeMessage message = this.mailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(recipient);
        helper.setFrom(this.mailSendingProperties.getFromAddress());
        helper.setReplyTo(this.mailSendingProperties.getNoReplyAddress() != null ? this.mailSendingProperties.getNoReplyAddress() : this.mailSendingProperties.getFromAddress());
        helper.setSubject(subject);
        helper.setText(body, false); // set text as plain text

        return message;
    }
}
