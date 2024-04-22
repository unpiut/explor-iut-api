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
package explorateurIUT.security.mailQuota.services;

import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import java.util.Collection;
import org.springframework.lang.Nullable;

/**
 *
 * @author rvenant
 */
public interface IPQuotaValidator {

    /**
     * Valide the Client IP that initiate a request agains its per-ip counter
     * limit and per-dept-and-ip counter limits.
     *
     * @param clientIP : client IP
     * @param deptIds : list of departement id. May be null or empty
     * @return false if one of the limits has already been reached, true
     * otherwise
     * @throws ValidationException if given parameters are invalid
     */
    boolean validateIPRequest(@NotBlank String clientIP, @Nullable Collection<String> deptIds) throws ValidationException;

    /**
     * update the client IP quota counter limit and per-dept-and-ip counter
     * limit.
     *
     * @param clientIP : client IP
     * @param deptIds : list of departement id. May be null or empty
     * @throws ValidationException if given parameters are invalid
     */
    void updateIPRequestCounter(@NotBlank String clientIP, @Nullable Collection<String> deptIds) throws ValidationException;

    /**
     * Remove all client IP quota whose all counters are outdated.
     */
    void cleanOutdatedQuotas();
}
