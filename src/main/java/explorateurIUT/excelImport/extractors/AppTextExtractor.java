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
package explorateurIUT.excelImport.extractors;

import java.util.Iterator;
import java.util.function.Consumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import explorateurIUT.excelImport.model.ExcelAppText;

/**
 *
 * @author Julien Fourdan
 */
public class AppTextExtractor implements SheetExtractor<ExcelAppText> {

    private static final Log LOG = LogFactory.getLog(AppTextExtractor.class);

    @Override
    public void extractEntities(XSSFSheet sheet, Consumer<ExcelAppText> entityConsumer) {
        LOG.debug("Loading excel sheet " + sheet.getSheetName());
        //Iterate over rows
        final Iterator<Row> itRows = sheet.rowIterator();
        //Skip first row (headings)
        itRows.next();

        //Iterate over remaining lines to complete IUT Info
        ExcelAppText currentTexte = null;

        while (itRows.hasNext()) {
            //Iterate cells
            final Iterator<Cell> itCells = itRows.next().cellIterator();
            if (!itCells.hasNext()) {
                LOG.debug("Having a row without any cells");
                continue;
            }
            while (itCells.hasNext()) {
                final Cell cell = itCells.next();
                String rawValue = ExtractorUtils.extractCellValue(cell);
                if (rawValue == null || rawValue.isBlank()) {
                    continue;
                }
                rawValue = rawValue.trim();
                final int columnIdx = cell.getAddress().getColumn();
                switch (columnIdx) {
                    case 0 -> { // Texte code, new Texte
                        if (currentTexte != null) {
                            LOG.warn("Texte without any content will not be retrieved: " + currentTexte.getCode());
                        }
                        LOG.debug("Create new Texte of name " + rawValue);
                        currentTexte = new ExcelAppText(rawValue);
                    }
                    case 1 -> { // Texte content
                        if (currentTexte == null) {
                            LOG.warn("New Texte content cell with no current Texte: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            currentTexte.setContent(rawValue);
                            entityConsumer.accept(currentTexte);
                            currentTexte = null;
                        }
                    }
                    default ->
                        LOG.debug("Outside scope cell of adress: " + cell.getAddress().formatAsR1C1String());
                }
            }
        }

        if (currentTexte != null) {
            LOG.warn("Texte without any content will not be retrieved: " + currentTexte.getCode());
        }
    }
}
