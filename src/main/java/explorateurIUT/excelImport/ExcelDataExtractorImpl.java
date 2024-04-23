package explorateurIUT.excelImport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.UnsupportedFileFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import explorateurIUT.excelImport.consumers.AppTextConsumer;
import explorateurIUT.excelImport.consumers.BUTConsumer;
import explorateurIUT.excelImport.consumers.IUTConsumer;
import explorateurIUT.excelImport.extractors.AppTextExtractor;
import explorateurIUT.excelImport.extractors.BUTExtractor;
import explorateurIUT.excelImport.extractors.IUTExtractor;
import jakarta.validation.constraints.NotNull;

public class ExcelDataExtractorImpl implements ExcelDataExtractor {
    AppDataProperties appDataProperties;
    private static final Log LOG = LogFactory.getLog(ExcelDataExtractorImpl.class);

    @Autowired
    public ExcelDataExtractorImpl(AppDataProperties appDataProperties){
        this.appDataProperties = appDataProperties;
    }

    @Override
    public void extractFromAppProperties(@NotNull AppTextConsumer appTextConsumer, @NotNull IUTConsumer iutConsumer,
            @NotNull BUTConsumer butConsumer) throws IOException {
                try (InputStream fis = new FileInputStream(new File(this.appDataProperties.getFilePath()))) {
                    this.extractFromInputStream(appTextConsumer, iutConsumer, butConsumer, fis);
                } 
    }

    @Override
    public void extractFromInputStream(@NotNull AppTextConsumer appTextConsumer, @NotNull IUTConsumer iutConsumer,
            @NotNull BUTConsumer butConsumer, @NotNull InputStream inputStream) throws IOException {
                try {
                    final XSSFWorkbook wb = new XSSFWorkbook(inputStream);
                    // First we process BUT
                    final BUTExtractor butExtractor = new BUTExtractor();
                    butExtractor.extractEntities(wb.getSheet(this.appDataProperties.getButSheetName()), butConsumer);
                    // Then we process DUT
                    final IUTExtractor iutExtractor = new IUTExtractor();
                    iutExtractor.extractEntities(wb.getSheet(this.appDataProperties.getIutSheetName()), iutConsumer);
                final AppTextExtractor appTextExtractor = new AppTextExtractor();
                appTextExtractor.extractEntities(wb.getSheet(this.appDataProperties.getAppTextSheetName()), appTextConsumer);
                } catch (IOException | UnsupportedFileFormatException ex) {
                    LOG.warn(String.format("Cannot load score file \"%s\": %s", this.appDataProperties.getFilePath(), ex.getMessage()));
                }
                LOG.info("ETL done.");
    }


}
