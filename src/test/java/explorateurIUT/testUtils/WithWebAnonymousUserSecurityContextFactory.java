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
package explorateurIUT.testUtils;

import java.util.List;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 *
 * @author rvenant
 */
public class WithWebAnonymousUserSecurityContextFactory implements WithSecurityContextFactory<WithWebAnonymousUser> {

    @Override
    public SecurityContext createSecurityContext(WithWebAnonymousUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        WebAuthenticationDetails authDetails = new WebAuthenticationDetails(annotation.inetAddress(), null);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        User anonUser = new User("anonymous", "###", authorities);

        AnonymousAuthenticationToken authToken = new AnonymousAuthenticationToken("aKey", anonUser, authorities);
        authToken.setDetails(authDetails);

        context.setAuthentication(authToken);
        return context;
    }

}
