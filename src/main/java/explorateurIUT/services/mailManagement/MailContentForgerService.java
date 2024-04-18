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

import java.util.List;

import explorateurIUT.model.MailIUTRecipient;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;

/**
 * Service for forging final mail content (subject, body)
 *
 * @author Remi Venant
 */
public interface MailContentForgerService {

    /**
     * Creation of the final body with the fusion of first form informations and
     * original body
     *
     * @param mailSendingRequest: mail sending request
     * @return the mail body
     * @throws ValidationException if given parameters are invalid
     */
    String createBody(@NotNull MailSendingRequest mailSendingRequest) throws ValidationException;

    /**
     * Creation of the list of mail destinations with their departments
     *
     * @param mailSendingRequest
     * @return
     * @throws ValidationException
     */
    List<MailIUTRecipient> createIUTMailingList(@NotNull MailSendingRequest mailSendingRequest) throws ValidationException;

    String createConfirmationMailSubject();

    String createConfirmationMailBody(String contactIdentity, String confirmationUrl);
}
