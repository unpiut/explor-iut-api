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

import explorateurIUT.model.PendingMailIUTRecipient;
import explorateurIUT.model.PendingMail;
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

import explorateurIUT.model.PendingMailAttachement;
import explorateurIUT.services.mailManagement.MailRequestAttachement;
import explorateurIUT.services.mailManagement.MailSendingProperties;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
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
    private final MailSendingProperties mailSendingProp;

    @Autowired
    public MailManagementServiceImpl(MailContentForgerService contentForgerSvc,
            MailContentValidationService validationSvc, MailSendingRequestTokenService tokenSvc,
            MailSendingService sendingSvc, PendingMailRepository pendingMailRepo,
            MailSendingProperties mailSendingProp) {
        this.contentForgerSvc = contentForgerSvc;
        this.validationSvc = validationSvc;
        this.tokenSvc = tokenSvc;
        this.sendingSvc = sendingSvc;
        this.pendingMailRepo = pendingMailRepo;
        this.mailSendingProp = mailSendingProp;
    }

    @Transactional
    @Override
    public LocalDateTime requestMailSending(MailSendingRequest sendingRequest, URI serverBaseURI) throws ValidationException, IOException {
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

        // Create initial pending mail 
        LOG.debug("Save pending mail in database");
        PendingMail pendingMail = new PendingMail(sendingRequest.subject(), body,
                sendingRequest.contactMail(), sendingRequest.contactIdentity());
        LOG.debug("Extract mailing list of iut");
        // Extract the mailing list
        final List<PendingMailIUTRecipient> iutMailingList = this.contentForgerSvc.createIUTMailingList(pendingMail, sendingRequest);
        // if list is empty -> exception
        if (iutMailingList.isEmpty()) {
            throw new IllegalArgumentException("No contact mail to send mail"); // TODO: precise the exception
        }
        // Add list to pending mail
        pendingMail.setRecipients(new HashSet<>(iutMailingList));

        // Add attachements if any
        LOG.debug("Add potential attachements");
        if (sendingRequest.attachements() != null) {
            final HashSet<PendingMailAttachement> attachements = new HashSet<>();
            for (MailRequestAttachement rqAtt : sendingRequest.attachements()) {
                PendingMailAttachement pma = new PendingMailAttachement(pendingMail, rqAtt.fileName(), rqAtt.file().getContentType(), rqAtt.file().getBytes());
                attachements.add(pma);
            }
            pendingMail.setAttachements(attachements);
        }

        // save pending mail repository
        LOG.debug("Save pending mail in database");
        pendingMail = this.pendingMailRepo.save(pendingMail);

        // Prepare validation token
        LOG.debug("Create validation token");
        TokenClearInfo tci = new TokenClearInfo(pendingMail.getId(), pendingMail.getCreationDateTime());
        final String validationToken = this.tokenSvc.createValidationToken(tci.getRepresentation());

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
        TokenClearInfo tci = new TokenClearInfo(pendingMail.getId(), pendingMail.getCreationDateTime());
        final String validationToken = this.tokenSvc.createValidationToken(tci.getRepresentation());
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
        return this.pendingMailRepo.clearMailsByCreationDateTimeBefore(LocalDateTime.now().minusHours(MAX_HOURS));
    }

    @Transactional
    @Override
    public void confirmMailSendingRequest(String confirmationToken) throws ValidationException, NoSuchElementException {
        final String clearToken = tokenSvc.decodeToken(confirmationToken);
        final TokenClearInfo tkInfo = TokenClearInfo.fromRepresentation(clearToken);
        // Retrieve the pending mail
        final Optional<PendingMail> possibleMail = pendingMailRepo.findById(tkInfo.pendingMailId());
        if (!possibleMail.isPresent()) {
            throw new NoSuchElementException("Mail not found");
        }
        final PendingMail mail = possibleMail.get();
        // Retrieve all potential attachement related to the pending mail
        try {
            // send the mail to iuts
            if (this.mailSendingProp.getTestingMailAddress() == null || this.mailSendingProp.getTestingMailAddress().isEmpty()) {
                // send the mail to iuts
                for (PendingMailIUTRecipient iut : mail.getRecipients()) {
                    String specificBody = contentForgerSvc.createSpecificBody(mail.getBody(), iut.getDepartementCodes());
                    LOG.debug("Mail to send subject: " + mail.getSubject());
                    LOG.debug("Mail to send body: ");
                    LOG.debug(specificBody);
                    sendingSvc.sendMailToIUT(iut.getMailAddress(), mail.getReplyTo(), mail.getSubject(), specificBody, mail.getAttachements());
                }
            } else {
                LOG.info("Sending mail to testing adress");
                // Compute list of fake departement codes that include IUT mail adresse and site for testing the mail
                List<String> testingDepartementCodes = mail.getRecipients().stream()
                        .flatMap((mailIUTRecipient) -> mailIUTRecipient.getDepartementCodes().stream()
                        .map(code -> mailIUTRecipient.getMailAddress() + "#" + code))
                        .toList();
                String specificBody = contentForgerSvc.createSpecificBody(mail.getBody(), testingDepartementCodes);
                LOG.debug("Mail to send subject: " + mail.getSubject());
                LOG.debug("Mail to send body: ");
                LOG.debug(specificBody);
                sendingSvc.sendMailToIUT(this.mailSendingProp.getTestingMailAddress(), mail.getReplyTo(), mail.getSubject(), specificBody, mail.getAttachements());
            }
            // remove the pending mail with all its recipient and its attachements
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

    private static record TokenClearInfo(Long pendingMailId, LocalDateTime creationDateTime) {

        public String getRepresentation() {
            return Long.toString(this.pendingMailId) + "##" + creationDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
        }

        public static TokenClearInfo fromRepresentation(String repr) {
            String[] splits = repr.split("##");
            Long id = Long.valueOf(splits[0]);
            LocalDateTime cdt = LocalDateTime.parse(splits[1], DateTimeFormatter.ISO_DATE_TIME);
            return new TokenClearInfo(id, cdt);
        }
    }
}
