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
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.mail.MailException;

/**
 *
 * @author Remi Venant
 */
public interface MailSendingService {

    /**
     * Send a mail to several IUT reciepients.
     *
     * @param recipients mail addresses of IUTs
     * @param replyTo mail address of contact
     * @param subject mail subject
     * @param body mail body
     * @param attachements possible attachment (can be null)
     * @throws ValidationException if given parameters are invalid
     * @throws MailException if unable to send mail
     * @throws MessagingException if unable to create mail message
     */
    void sendMailToIUT(@NotEmpty Collection<@NotNull @Email String> recipients,
            @NotNull @Email String replyTo,
            @NotBlank String subject,
            @NotBlank String body,
            List<@NotNull GridFsResource> attachements) throws ValidationException, MailException, MessagingException;

    /**
     * Send a mail to a particular contact.
     *
     * @param recipient mail address of contact
     * @param subject mail subject
     * @param body mail body
     * @throws ValidationException if given parameters are invalid
     * @throws MailException if unable to send mail
     * @throws MessagingException if unable to create mail message
     */
    void sendMailToContact(@NotNull @Email String recipient, @NotBlank String subject,
            @NotBlank String body) throws ValidationException, MailException, MessagingException;

}
