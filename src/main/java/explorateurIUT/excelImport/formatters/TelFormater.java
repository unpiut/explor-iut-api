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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Remi Venant
 */
public class TelFormater {

    private final static Pattern TEL_PATTERN = Pattern.compile("^\\s*0?[^\\d]*(\\d)[^\\d]*(\\d)[^\\d]*(\\d)[^\\d]*(\\d)[^\\d]*(\\d)[^\\d]*(\\d)[^\\d]*(\\d)[^\\d]*(\\d)[^\\d]*(\\d)[^\\d]*$", Pattern.CASE_INSENSITIVE);

    public static String matchesAndRetrieve(String text) {
        if (text == null) {
            return null;
        }
        final Matcher m = TEL_PATTERN.matcher(text);
        if (!m.matches()) {
            return null;
        }
        final StringBuilder sb = new StringBuilder("0");
        for (int i = 1; i <= m.groupCount(); i++) {
            sb.append(m.group(i));
        }
        return sb.toString();
    }

}
