package explorateurIUT.excelImport.extractors;

import java.util.Iterator;
import java.util.function.Consumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import explorateurIUT.excelImport.model.ExcelTexte;

public class TexteExtractor implements SheetExtractor<ExcelTexte>{
    private static final Log LOG = LogFactory.getLog(TexteExtractor.class);

    @Override
    public void extractEntities(XSSFSheet sheet, Consumer<ExcelTexte> entityConsumer) {
        LOG.debug("Loading excel sheet " + sheet.getSheetName());
        //Iterate over rows
        final Iterator<Row> itRows = sheet.rowIterator();
        //Skip first row (headings)
        itRows.next();

        //Iterate over remaining lines to complete IUT Info
        ExcelTexte currentTexte = null;

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
                            entityConsumer.accept(currentTexte);
                        }
                        LOG.debug("Create new Texte of name " + rawValue);
                        currentTexte = new ExcelTexte(rawValue);
                    }
                    case 2 -> { // Texte content
                        if (currentTexte == null) {
                            LOG.warn("New Texte content cell with no current Texte: " + cell.getAddress().formatAsR1C1String());
                        } else {
                            currentTexte.setContent(rawValue);
                        }
                    }
                    default ->
                        LOG.debug("Outside scope cell of adress: " + cell.getAddress().formatAsR1C1String());
                }
            }
        }

        if (currentTexte != null) {
            entityConsumer.accept(currentTexte);
        }
    }
}
