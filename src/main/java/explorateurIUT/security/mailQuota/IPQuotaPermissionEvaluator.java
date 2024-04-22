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
package explorateurIUT.security.mailQuota;

import explorateurIUT.security.mailQuota.services.IPQuotaValidator;
import java.io.Serializable;
import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 *
 * @author rvenant
 */
public class IPQuotaPermissionEvaluator implements PermissionEvaluator {

    private static final Log LOG = LogFactory.getLog(IPQuotaPermissionEvaluator.class);

    private final IPQuotaValidator ipQuotaValidator;

    public IPQuotaPermissionEvaluator(IPQuotaValidator ipQuotaValidator) {
        this.ipQuotaValidator = ipQuotaValidator;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        final String perm = ((String) permission).toLowerCase();
        if (perm == null) {
            LOG.warn("Unexcepted null permission request");
            return false;
        }
        final String ipAdress = this.extractIPFromAuthentication(authentication);
        if (ipAdress == null) {
            LOG.warn("Unable to extract ip address from client");
            return false;
        }
        Collection<String> deptIds = null;
        if (targetDomainObject != null && targetDomainObject instanceof Collection col) {
            deptIds = col;
        }
        switch (perm) {
            case "quota-met" -> {
                return this.ipQuotaValidator.validateIPRequest(ipAdress, deptIds);
            }
            case "quota-update" -> {
                this.ipQuotaValidator.updateIPRequestCounter(ipAdress, deptIds);
                return true;
            }
            default -> {
                LOG.warn("Unexcepted permission request: " + perm);
                return false;
            }
        }
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (targetId == null && targetType == null) {
            return this.hasPermission(authentication, null, permission);
        }
        LOG.warn("Unexcepted hasPermission requestion with given targetId");
        return false;
    }

    private String extractIPFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication instanceof AbstractAuthenticationToken authToken) {
            if (authToken.getDetails() != null && authToken.getDetails() instanceof WebAuthenticationDetails webAuthDet) {
                return webAuthDet.getRemoteAddress();
            }
        }
        return null;
    }
}
