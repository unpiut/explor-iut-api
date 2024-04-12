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
public interface MailContentValidationService {
    /**
     * Sanitize the mail informations
     * @param informations : Informations of the first form (name, entreprise name, function of the client and his mail)
     * @param body : body of the mail
     * @param subject : subject of the mail
     * @return true if the sanitizer detect nothing
     * @throws ValidationException if given parameters are invalid
     */
    boolean sanitizer(@NotBlank String[] informations,@NotBlank String body,@NotBlank String subject) throws ValidationException;
}
