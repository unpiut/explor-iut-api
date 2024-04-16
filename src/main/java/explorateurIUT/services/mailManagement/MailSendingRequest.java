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

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author Remi Venant
 */
public record MailSendingRequest(
        @NotEmpty
        Collection<@NotNull @Pattern(regexp = "[abcdef0-9]{24}", flags = Pattern.Flag.CASE_INSENSITIVE) String> iutIds,
        @NotBlank
        @Size(max = 200)
        String contactIdentity,
        @NotBlank
        @Size(max = 200)
        String contactCompany,
        @NotBlank
        @Size(max = 200)
        String contactFunction,
        @Email
        @Size(max = 200)
        String contactMail,
        @NotBlank
        String subject,
        @NotBlank
        @Size(max = 2000)
        String body,
        List<@NotNull @Valid MailRequestAttachement> attachements) {

}
