/*
 * Copyright (C) 2023 IUT Laval - Le Mans Université.
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
package explorateurIUT.excelImport.formatters;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Remi Venant
 */
public class UrlFormater {

    private static final Log LOG = LogFactory.getLog(UrlFormater.class);

    public static String matchesAndRetrieve(String text) {
        if (text == null) {
            return null;
        }
        try {
            URI u = new URI(text);
            return u.toASCIIString();
        } catch (URISyntaxException ex) {
            if (text.startsWith("http")) {
                LOG.warn("Error while parsing text to url starting with http: " + ex.getMessage());
            }
            return null;
        }
    }

}
