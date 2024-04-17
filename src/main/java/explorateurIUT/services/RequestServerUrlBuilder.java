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
package explorateurIUT.services;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Remi Venant
 */
@Component
@RequestScope
public class RequestServerUrlBuilder {

    private final HttpServletRequest request;

    @Autowired
    public RequestServerUrlBuilder(HttpServletRequest request) {
        this.request = request;
    }

    public URI buildServerBaseURI() {
        return this.buildBaseURI(request.getScheme(), request.getServerName(), request.getServerPort());
    }

    protected URI buildBaseURI(String scheme, String serverName, int serverPort) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(serverName);
        if ((scheme.equalsIgnoreCase("http") && serverPort != 80) || (scheme.equalsIgnoreCase("https") && serverPort != 443)) {
            uriComponentsBuilder.port(serverPort);
        }
        return uriComponentsBuilder.build().toUri();
    }
}
