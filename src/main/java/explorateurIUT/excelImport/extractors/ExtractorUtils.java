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
package explorateurIUT.excelImport.extractors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import static org.apache.poi.ss.usermodel.CellType.BLANK;
import static org.apache.poi.ss.usermodel.CellType.BOOLEAN;
import static org.apache.poi.ss.usermodel.CellType.ERROR;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;
import static org.apache.poi.ss.usermodel.CellType._NONE;
import org.apache.poi.ss.usermodel.Hyperlink;

/**
 *
 * @author Remi Venant
 */
public class ExtractorUtils {

    private static final Log LOG = LogFactory.getLog(ExtractorUtils.class);

    private ExtractorUtils() {
    }

    public static String extractCellValue(Cell cell) {
        // Extract hyperlink if any
        Hyperlink hyperlink = cell.getHyperlink();
        if (hyperlink != null) {
            String linkAddr = hyperlink.getAddress();
            if (linkAddr != null) {
                linkAddr = linkAddr.trim();
                if (!linkAddr.isBlank()) {
                    return linkAddr;
                }
            }
        }
        switch (cell.getCellType()) {
            case STRING -> {
                return cell.getStringCellValue().trim();
            }
            case NUMERIC -> {
                return Double.toString(cell.getNumericCellValue());
            }
            case BOOLEAN -> {
                return Boolean.toString(cell.getBooleanCellValue());
            }
            case BLANK -> {
                return null;
            }
            case _NONE -> {
                return null;
            }
            case ERROR -> {
                return cell.getCellFormula();
            }
            default -> {
                LOG.warn("Cell not valid! CellType: " + cell.getCellType());
                return null;
            }
        }
    }

    public static Integer extractIntegerValue(Cell cell) {
        switch (cell.getCellType()) {
            case NUMERIC -> {
                return (int) cell.getNumericCellValue();
            }
            case BLANK -> {
                return null;
            }
            case _NONE -> {
                return null;
            }
            default -> {
                LOG.warn("Cell not valid for numeric value! CellType: " + cell.getCellType());
                return null;
            }
        }
    }

    public static boolean isComment(String value) {
        return value.startsWith("#") || value.startsWith("/");
    }
}
