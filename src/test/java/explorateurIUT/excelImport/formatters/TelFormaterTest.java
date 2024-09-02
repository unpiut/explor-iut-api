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
package explorateurIUT.excelImport.formatters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Rémi Venant
 */
public class TelFormaterTest {

    public TelFormaterTest() {
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
     * Test of matchesAndRetrieve method, of class TelFormater.
     */
    @Test
    public void testMatchesAndRetrieve() {
        System.out.println("matchesAndRetrieve");
        String[] givenTels = new String[]{"0247324445", "02 47 32 44 45", "02.47.32.44.45", " 02  47 32   44 45  "};
        String expectedTel = givenTels[0];
        for (String tel : givenTels) {
            String res = TelFormater.matchesAndRetrieve(tel);
            assertThat(res).as("a valid tel number should be extracted").isEqualTo(expectedTel);
        }

        String res = TelFormater.matchesAndRetrieve("0247324a45");
        assertThat(res).as("an invalid tel number should be null").isNull();

        res = TelFormater.matchesAndRetrieve("0247324445 / 0247324446");
        assertThat(res).as("an double tel number should not be null").isNotNull();
    }
    
    @Test
    public void testMatchesAndRetrieveMultiple() {
        System.out.println("matchesAndRetrieve");
        String[] givenTels = new String[]{"0247324445 / 0245652232", "0247324445 ;   02.45.65.22.32", "0247324445,02-45-65.22.32", "0247324445   / ; ,  /   0245652232 /,;"};
        String expectedTel = givenTels[0];
        for (String tel : givenTels) {
            String res = TelFormater.matchesAndRetrieve(tel);
            assertThat(res).as("a valid tel number should be extracted").isEqualTo(expectedTel);
        }
    }

    @Test 
    public void testNewMatcher() {
       Pattern p = Pattern.compile("^\\s*0?(\\d)[\\s\\.-]*(\\d{2})[\\s\\.-]*(\\d{2})[\\s\\.-]*(\\d{2})[\\s\\.-]*(\\d{2})\\s*$");
       Matcher m = p.matcher("02.42.43.34.55");
       assertThat(m).as("tel matches").matches();
       assertThat(m.groupCount()).as("has proper group count").isEqualTo(5);
        System.out.println("matcher num groups: " + m.groupCount());
       for (int i = 0 ; i <= m.groupCount(); i++) {
           System.out.println("Matcher group " + i + ": " + m.group(i));
       }
    }
}
