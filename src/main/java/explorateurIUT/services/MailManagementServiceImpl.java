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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.stream.Stream;
import com.mongodb.client.gridfs.model.GridFSFile;

/**
 *
 * @author Remi Venant
 */
@Service
@Validated
public class MailManagementServiceImpl implements MailManagementService {

    private static final Log LOG = LogFactory.getLog(MailManagementServiceImpl.class);

    private final MailContentForgerService contentForgerSvc;
    private final MailContentValidationService validationSvc;
    private final MailSendingRequestTokenService tokenSvc;
    private final MailSendingService sendingSvc;
    private final PendingMailRepository pendingMailRepo;
    private final PendingMailAttachementRepository attachementRepo;

    @Autowired
    public MailManagementServiceImpl(MailContentForgerService contentForgerSvc,
            MailContentValidationService validationSvc, MailSendingRequestTokenService tokenSvc,
            MailSendingService sendingSvc, PendingMailRepository pendingMailRepo, PendingMailAttachementRepository attachementRepo) {
        this.contentForgerSvc = contentForgerSvc;
        this.validationSvc = validationSvc;
        this.tokenSvc = tokenSvc;
        this.sendingSvc = sendingSvc;
        this.pendingMailRepo = pendingMailRepo;
        this.attachementRepo = attachementRepo;
    }

    @Transactional
    @Override
    public LocalDateTime requestMailSending(MailSendingRequest sendingRequest, URI serverBaseURI) throws ValidationException, IOException {
        LOG.debug("Extract mailing list of iut");
        // Extract the mailing list
        final List<String> iutMailingList = this.contentForgerSvc.createIUTMailingList(sendingRequest);
        // if list is empty -> exception
        if (iutMailingList.isEmpty()) {
            throw new IllegalArgumentException("No contact mail to send mail"); // TODO: precise the exception
        }

        // Forge the body
        LOG.debug("Forge body");
        String body = this.contentForgerSvc.createBody(sendingRequest);

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
                new PendingMail(iutMailingList, sendingRequest.subject(), body, sendingRequest.contactMail()));
        // Prepare validation token
        LOG.debug("Create validation token");
        final String validationToken = this.tokenSvc.createValidationToken(pendingMail.getId());
        // Save attachement
        LOG.debug("Save potential attachements");
        sendingRequest.attachements().forEach((a) -> this.attachementRepo.save(a, pendingMail));

        // Create and send confirmation mail
        LOG.debug("Create and send confirmation mail");
        try {
            this.createAndSendConfirmationMail(sendingRequest.contactIdentity(), sendingRequest.contactMail(), serverBaseURI, validationToken);
        } catch (MessagingException ex) {
            LOG.error("Unable to send the confirmation mail: ", ex);
        }
        // Update pending mail lastConfirmationMail
        this.pendingMailRepo.findAndSetLastConfirmationMailById(pendingMail.getId(), LocalDateTime.now());

        // return the create datetime of the pending mail
        return pendingMail.getCreationDateTime();
    }

    @Override
    public void resendConfirmationMail(LocalDateTime creationDatetime, String contactMail, URI serverBaseURI) throws ValidationException, NoSuchElementException {
        LOG.debug("resendConfirmationMail: TO IMPLEMENT!");
        final Optional<PendingMail> mail = pendingMailRepo.findByCreationDateTimeAndReplyTo(creationDatetime, contactMail);
        if(mail.isPresent()){
            PendingMail pendingMail = mail.get();
            if(pendingMail.getLastConfirmationMail().isBefore(LocalDateTime.now().minusMinutes(2)) || pendingMail.getLastConfirmationMail() == null) {
                pendingMailRepo.findAndSetLastConfirmationMailById(pendingMail.getId(),LocalDateTime.now());
                // Prepare validation token
        LOG.debug("Create validation token");
        final String validationToken = this.tokenSvc.createValidationToken(pendingMail.getId());
        // Save attachement
        // Create and send confirmation mail
        LOG.debug("Create and send confirmation mail");
        try {
            this.createAndSendConfirmationMail(pendingMail.getReplyTo(), pendingMail.getReplyTo(), serverBaseURI, validationToken); //I don't see where get the contactName
        } catch (MessagingException ex) {
            LOG.error("Unable to send the confirmation mail: ", ex);
        }
        // Update pending mail lastConfirmationMail
        this.pendingMailRepo.findAndSetLastConfirmationMailById(pendingMail.getId(), LocalDateTime.now());
            }
        }
        // If found check that last confirmation mail has been sent more than X minutes ago (or never sent)
        // If ok, regenerate token and send a new confirmation mail
    }

    @Transactional
    @Override
    public int removeOutdatedPendingMailRequest() {
        LOG.debug("removeOutdatedPendingMailRequest: TO IMPLEMENT!");
        pendingMailRepo.deleteByCreationDateTimeBefore(LocalDateTime.now().minusHours(3));
        attachementRepo.deleteByCreationDateTimeBefore(LocalDateTime.now().minusHours(3));
        // remove all outdate pending Mail
        return 0;
    }

    @Transactional
    @Override
    public void confirmMailSendingRequest(String confirmationToken) throws ValidationException, NoSuchElementException, MessagingException {
        LOG.debug("confirmMailSendingRequest: TO IMPLEMENT!");
        final String mailId = tokenSvc.decodeToken(confirmationToken);
        final Optional<PendingMail> possibleMail = pendingMailRepo.findById(mailId);
        if(!possibleMail.isPresent()){
            throw new NoSuchElementException("Mail not found");
        }
        final PendingMail mail = possibleMail.get();
        final Stream<GridFSFile> attachements = attachementRepo.streamByPendingMailId(mail.getId());
        sendingSvc.sendMailToIUT(mail.getIUTmailRecipients(),mail.getReplyTo(),mail.getSubject(),mail.getBody(),attachements);
        attachementRepo.deleteByPendingMailId(mail.getId());
        pendingMailRepo.delete(mail);
    }

    private void createAndSendConfirmationMail(String contactIdentity, String recipientMailAddress, URI serverBaseURI, String token) throws MessagingException {
        LOG.debug("createAndSendConfirmationMail: TO IMPLEMENT!");
        UriComponents uriComponents = UriComponentsBuilder.fromUri(serverBaseURI)
                .path("/api/v1/mail/validate")
                .queryParam("t", token)
                .build();
        final String validationURI = uriComponents.toUriString();
        final String mailSubject = this.contentForgerSvc.createConfirmationMailSubject();
        final String mailBody = this.contentForgerSvc.createConfirmationMailBody(contactIdentity, validationURI);
        this.sendingSvc.sendMailToContact(recipientMailAddress, mailSubject, mailBody);
    }
}
