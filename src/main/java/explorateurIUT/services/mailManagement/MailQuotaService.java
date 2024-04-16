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

import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;

/**
 *
 * @author Remi Venant
 */
public interface MailQuotaService {
   
    /**
     * Ask the DB if the simultaneous mail limit is reached
     */
    void checkGlobalRequestLimit();

    /**
     * Ask the DB if the Client IP address's mail limit is reached
     * @param clientIP : IP of the form's user
     * @throws ValidationException if given parameters are invalid
     */
    void checkRequestPerClientLimit(@NotBlank String clientIP) throws ValidationException;

    
    /**
     * Ask the DB if the Client IP address's mail already send mail to this dep in the 72h
     * @param clientIP IP of the client
     * @param idDepList List of the Id of the departments selected
     * @throws ValidationException
     */
    void checkRequestPerClientPerDepLimit(@NotBlank String clientIP, List<String> idDepList) throws ValidationException;
    
}
