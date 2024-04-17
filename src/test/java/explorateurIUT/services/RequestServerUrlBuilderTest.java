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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 *
 * @author Remi Venant
 */
public class RequestServerUrlBuilderTest {
    
    public RequestServerUrlBuilderTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of buildServerBaseURI method, of class RequestServerUrlBuilder.
     */
    @Test
    public void testBuildServerBaseURI() {
        MockHttpServletRequest rq = new MockHttpServletRequest("GET", "https://paul.com/mail/send");
        rq.setScheme("https");
        rq.setServerName("paul.com");
        rq.setServerPort(443);
        RequestServerUrlBuilder instance = new RequestServerUrlBuilder(rq);
        assertThat(instance.buildServerBaseURI()).as("Generated URI from request is ok").isEqualTo(URI.create("https://paul.com"));
        
        rq = new MockHttpServletRequest("GET", "http://pilou.paul.com:80/mail/send");
        rq.setScheme("http");
        rq.setServerName("pilou.paul.com");
        rq.setServerPort(80);
        instance = new RequestServerUrlBuilder(rq);
        assertThat(instance.buildServerBaseURI()).as("Generated URI from request with explicit default port is ok").isEqualTo(URI.create("http://pilou.paul.com"));
        
        rq = new MockHttpServletRequest("GET", "https://pilou.paul.com:8181/mail/send");
        rq.setScheme("https");
        rq.setServerName("pilou.paul.com");
        rq.setServerPort(8181);
        instance = new RequestServerUrlBuilder(rq);
        assertThat(instance.buildServerBaseURI()).as("Generated URI from request with non default port is ok").isEqualTo(URI.create("https://pilou.paul.com:8181"));
    }

    /**
     * Test of buildBaseURI method, of class RequestServerUrlBuilder.
     */
    @Test
    public void testBuildBaseURI() {
        RequestServerUrlBuilder instance = new RequestServerUrlBuilder(null);
        URI result = instance.buildBaseURI("https", "paul.com", 443);
        assertThat(result).as("Generated URI with default port on https does not show the port").isEqualTo(URI.create("https://paul.com"));
        result = instance.buildBaseURI("http", "paul.com", 80);
        assertThat(result).as("Generated URI with default port on http does not show the port").isEqualTo(URI.create("http://paul.com"));
        result = instance.buildBaseURI("https", "paul.com", 80);
        assertThat(result).as("Generated URI with non default port does not show the port").isEqualTo(URI.create("https://paul.com:80"));
        result = instance.buildBaseURI("http", "paul.com", 443);
        assertThat(result).as("Generated URI with non default port does not show the port").isEqualTo(URI.create("http://paul.com:443"));
        result = instance.buildBaseURI("http", "paul.com", 8080);
        assertThat(result).as("Generated URI with non default port does not show the port").isEqualTo(URI.create("http://paul.com:8080"));
    }
    
}
