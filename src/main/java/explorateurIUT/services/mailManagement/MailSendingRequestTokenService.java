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

import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;

/**
 *
 * @author Remi Venant
 */
public interface MailSendingRequestTokenService {

    /**
     * Create a mail sending request validation token from its id
     *
     * @param mailSendingRequestId the request id
     * @return the token
     * @throws ValidationException if given parameters are invalid
     */
    String createValidationToken(@NotBlank String mailSendingRequestId) throws ValidationException;

    /**
     * Decode a sending request validation token and extract its id.
     *
     * @param token the token
     * @return the mail sending request id
     * @throws ValidationException if the token is null or blank
     * @throws IllegalArgumentException if the token cannot be decoded
     */
    String decodeToken(@NotBlank String token) throws ValidationException, IllegalArgumentException;
}
