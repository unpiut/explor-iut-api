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

import explorateurIUT.model.MailIUTRecipient;
import explorateurIUT.model.PendingMail;
import explorateurIUT.model.PendingMailAttachementRepository;
import explorateurIUT.model.PendingMailRepository;
import explorateurIUT.services.mailManagement.MailContentForgerService;
import explorateurIUT.services.mailManagement.MailContentValidationService;
import explorateurIUT.services.mailManagement.MailSendingRequest;
import explorateurIUT.services.mailManagement.MailSendingRequestTokenService;
import explorateurIUT.services.mailManagement.MailSendingService;
import jakarta.mail.MessagingException;
import jakarta.validation.ValidationException;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.mail.MailPreparationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.mongodb.client.gridfs.model.GridFSFile;
import explorateurIUT.services.mailManagement.MailSendingProperties;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;

/**
 *
 * @author Remi Venant
 */
@Service
@Validated
public class MailManagementServiceImpl implements MailManagementService {

    private static final Log LOG = LogFactory.getLog(MailManagementServiceImpl.class);
    private static final int MAX_HOURS = 3;
    private static final int MAX_MINUTES = 5;

    private final MailContentForgerService contentForgerSvc;
    private final MailContentValidationService validationSvc;
    private final MailSendingRequestTokenService tokenSvc;
    private final MailSendingService sendingSvc;
    private final PendingMailRepository pendingMailRepo;
    private final PendingMailAttachementRepository attachementRepo;
    private final MailSendingProperties mailSendingProp;

    @Autowired
    public MailManagementServiceImpl(MailContentForgerService contentForgerSvc,
            MailContentValidationService validationSvc, MailSendingRequestTokenService tokenSvc,
            MailSendingService sendingSvc, PendingMailRepository pendingMailRepo, PendingMailAttachementRepository attachementRepo, MailSendingProperties mailSendingProp) {
        this.contentForgerSvc = contentForgerSvc;
        this.validationSvc = validationSvc;
        this.tokenSvc = tokenSvc;
        this.sendingSvc = sendingSvc;
        this.pendingMailRepo = pendingMailRepo;
        this.attachementRepo = attachementRepo;
        this.mailSendingProp = mailSendingProp;
    }

    @Transactional
    @Override
    public LocalDateTime requestMailSending(MailSendingRequest sendingRequest, URI serverBaseURI) throws ValidationException, IOException {
        LOG.debug("Extract mailing list of iut");
        // Extract the mailing list
        final List<MailIUTRecipient> iutMailingList = this.contentForgerSvc.createIUTMailingList(sendingRequest);
        // if list is empty -> exception
        if (iutMailingList.isEmpty()) {
            throw new IllegalArgumentException("No contact mail to send mail"); // TODO: precise the exception
        }

        // Forge the body
        LOG.debug("Forge body");
        String body = this.contentForgerSvc.createGeneralBody(sendingRequest);

        // Validate (sanitizer) the different mail part that depends from use input: body, subject, replyTo (contact)
        LOG.debug("Validate mail parts");
        if (!this.validationSvc.isValid(body) || !this.validationSvc.isValid(sendingRequest.subject()) || !this.validationSvc.isValid(sendingRequest.contactMail())) {
            throw new IllegalArgumentException("Invalid mail part"); // TODO: precise the exception
        }
        // Validate attachement if any
        if (!this.validationSvc.isValid(sendingRequest.attachements())) {
            throw new IllegalArgumentException("Invalid mail attachement");
        }

        // Create and save pending mail repository
        LOG.debug("Save pending mail in database");
        final PendingMail pendingMail = this.pendingMailRepo.save(
                new PendingMail(iutMailingList, sendingRequest.subject(), body, sendingRequest.contactMail(), sendingRequest.contactIdentity()));
        // Prepare validation token
        LOG.debug("Create validation token");
        final String validationToken = this.tokenSvc.createValidationToken(pendingMail.getId());
        // Save attachement
        LOG.debug("Save potential attachements");
        if (!(sendingRequest.attachements() == null)) {
            sendingRequest.attachements().forEach((a) -> this.attachementRepo.save(a, pendingMail));
        }
        // Create and send confirmation mail
        LOG.debug("Create and send confirmation mail");
        try {
            this.createAndSendConfirmationMail(sendingRequest.contactIdentity(), sendingRequest.contactMail(), serverBaseURI, validationToken);

            // Update pending mail lastConfirmationMail
            this.pendingMailRepo.findAndSetLastConfirmationMailById(pendingMail.getId(), LocalDateTime.now());

            // return the create datetime of the pending mail
            return pendingMail.getCreationDateTime();
        } catch (MessagingException ex) {
            LOG.error("Unable to create the confirmation mail: ", ex);
            throw new MailPreparationException(ex);
        }
    }

    @Override
    public void resendConfirmationMail(LocalDateTime creationDatetime, String contactMail, URI serverBaseURI) throws ValidationException, NoSuchElementException {
        LOG.debug("resendConfirmationMail: TO IMPLEMENT!");
        final Optional<PendingMail> mail = pendingMailRepo.findByCreationDateTimeAndReplyTo(creationDatetime, contactMail);
        if (!mail.isPresent()) {
            throw new NoSuchElementException("Pending mail not found");
        }
        PendingMail pendingMail = mail.get();
        if (pendingMail.getLastConfirmationMail().isBefore(LocalDateTime.now().minusMinutes(MAX_MINUTES)) || pendingMail.getLastConfirmationMail() == null) {
            throw new IllegalArgumentException("Mail already sent int the last 5 minutes. Please try later");
        }
        pendingMailRepo.findAndSetLastConfirmationMailById(pendingMail.getId(), LocalDateTime.now());
        // Prepare validation token
        LOG.debug("Create validation token");
        final String validationToken = this.tokenSvc.createValidationToken(pendingMail.getId());
        // Save attachement
        // Create and send confirmation mail
        LOG.debug("Create and send confirmation mail");
        try {
            this.createAndSendConfirmationMail(pendingMail.getContactName(), pendingMail.getReplyTo(), serverBaseURI, validationToken);
            this.pendingMailRepo.findAndSetLastConfirmationMailById(pendingMail.getId(), LocalDateTime.now());
        } catch (MessagingException ex) {
            LOG.error("Unable to send the confirmation mail: ", ex);
            throw new MailPreparationException(ex);
        }
        // Update pending mail lastConfirmationMail
        // If found check that last confirmation mail has been sent more than X minutes ago (or never sent)
        // If ok, regenerate token and send a new confirmation mail
    }

    @Transactional
    @Override
    public int removeOutdatedPendingMailRequest() {
        pendingMailRepo.deleteByCreationDateTimeBefore(LocalDateTime.now().minusHours(MAX_HOURS));
        attachementRepo.deleteByCreationDateTimeBefore(LocalDateTime.now().minusHours(MAX_HOURS));
        // remove all outdate pending Mail
        return 0;
    }

    @Transactional
    @Override
    public void confirmMailSendingRequest(String confirmationToken) throws ValidationException, NoSuchElementException {
        final String mailId = tokenSvc.decodeToken(confirmationToken);
        // Retrieve the pending mail
        final Optional<PendingMail> possibleMail = pendingMailRepo.findById(mailId);
        if (!possibleMail.isPresent()) {
            throw new NoSuchElementException("Mail not found");
        }
        final PendingMail mail = possibleMail.get();
        // Retrieve all potential attachement related to the pending mail
        final List<GridFSFile> attachements = attachementRepo.streamByPendingMailId(mail.getId()).toList();
        Collection<String> mailIUT = mail.getIUTMailRecipients().stream().map((mailIut) -> mailIut.getMailAddress()).toList();
        try {
            // send the mail to iuts
            if (this.mailSendingProp.getTestingMailAddress() == null || this.mailSendingProp.getTestingMailAddress().isEmpty()) {
                // send the mail to iuts
                for (MailIUTRecipient iut : mail.getIUTMailRecipients()) {
                    String specificBody = contentForgerSvc.createSpecificBody(mail.getBody(), iut.getDepartementCodes());
                    LOG.debug("Mail to send subject: " + mail.getSubject());
                    LOG.debug("Mail to send body: ");
                    LOG.debug(specificBody);
                    sendingSvc.sendMailToIUT(iut.getMailAddress(), mail.getReplyTo(), mail.getSubject(), specificBody, attachements);
                }
            } else {
                LOG.info("Sending mail to testing adress");
                // Compute list of fake departement codes that include IUT mail adresse and site for testing the mail
                List<String> testingDepartementCodes = mail.getIUTMailRecipients().stream()
                        .flatMap((mailIUTRecipient) -> mailIUTRecipient.getDepartementCodes().stream()
                        .map(code -> mailIUTRecipient.getMailAddress() + "#" + code))
                        .toList();
                String specificBody = contentForgerSvc.createSpecificBody(mail.getBody(), testingDepartementCodes);
                LOG.debug("Mail to send subject: " + mail.getSubject());
                LOG.debug("Mail to send body: ");
                LOG.debug(specificBody);
                sendingSvc.sendMailToIUT(this.mailSendingProp.getTestingMailAddress(), mail.getReplyTo(), mail.getSubject(), specificBody, attachements);
            }
            // remove all attachements related to the pending mail
            attachementRepo.deleteByPendingMailId(mail.getId());
            // remove the pending mail
            pendingMailRepo.delete(mail);
        } catch (MessagingException ex) {
            LOG.error("Unable to send the confirmation mail: ", ex);
            throw new MailPreparationException(ex);
        }
    }

    @Override
    public void sendTestMail(String recipient) throws ValidationException, MailException {
        try {
            final String bodyTest = "Bonjour,\r\nCeci est un courriel de test.";
            final String subjectTest = "Test Mail ExplorIUT";
            LOG.debug("Sending a test mail to " + recipient);
            this.sendingSvc.sendMailToContact(recipient, subjectTest, bodyTest);
        } catch (MessagingException ex) {
            LOG.error("Unable to send the confirmation mail: ", ex);
            throw new MailPreparationException(ex);
        }
    }

    private void createAndSendConfirmationMail(String contactIdentity, String recipientMailAddress, URI serverBaseURI, String token) throws MessagingException {
        final String validationUrl = this.mailSendingProp.getValidationUrl();
        UriComponents uriComponents;
        final String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        if (validationUrl.toLowerCase().startsWith("http")) {
            uriComponents = UriComponentsBuilder.fromHttpUrl(validationUrl)
                    .queryParam("t", encodedToken)
                    .build();
        } else {
            uriComponents = UriComponentsBuilder.fromUri(serverBaseURI)
                    .path(validationUrl)
                    .queryParam("t", encodedToken)
                    .build();
        }
        final String validationURI = uriComponents.toUriString();
        final String mailSubject = this.contentForgerSvc.createConfirmationMailSubject();
        final String mailBody = this.contentForgerSvc.createConfirmationMailBody(contactIdentity, validationURI);
        this.sendingSvc.sendMailToContact(recipientMailAddress, mailSubject, mailBody);
    }
}
