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

import explorateurIUT.security.mailQuota.services.GlobalQuotaValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author rvenant
 */
public class GlobalQuotaFilter extends OncePerRequestFilter {

    private static final Log LOG = LogFactory.getLog(GlobalQuotaFilter.class);

    private RequestMatcher mailQuotaRequestMatcher;

    private final GlobalQuotaValidator globalQuotaValidator;

    public GlobalQuotaFilter(GlobalQuotaValidator globalQuotaValidator) {
        this.globalQuotaValidator = globalQuotaValidator;
        this.setFilterProcessesUrl("/mail");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // If the request matches the path matcher
        if (this.mailQuotaRequestMatcher.matches(request)) {
            // Validate the request against quota (may throw AccessDeniedException)
            if (!this.globalQuotaValidator.validateAndUpdateRequestCounter()) {
                throw new AccessDeniedException("Global max request count already reached");
            }
        }
        // If everything is ok, continue processing the request
        filterChain.doFilter(request, response);
    }

    public final void setFilterProcessesUrl(String filterProcessesUrl) {
        this.mailQuotaRequestMatcher = new AntPathRequestMatcher(filterProcessesUrl);
    }

}
