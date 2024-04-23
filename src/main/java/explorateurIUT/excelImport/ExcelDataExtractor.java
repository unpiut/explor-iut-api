package explorateurIUT.excelImport;

import java.io.IOException;
import java.io.InputStream;

import explorateurIUT.excelImport.consumers.AppTextConsumer;
import explorateurIUT.excelImport.consumers.BUTConsumer;
import explorateurIUT.excelImport.consumers.IUTConsumer;
import jakarta.validation.constraints.NotNull;

public interface ExcelDataExtractor {

    public void extractFromAppProperties(@NotNull AppTextConsumer appTextConsumer,@NotNull IUTConsumer iutConsumer,@NotNull BUTConsumer butConsumer) throws IOException;

    public void extractFromInputStream(@NotNull AppTextConsumer appTextConsumer,@NotNull IUTConsumer iutConsumer,@NotNull BUTConsumer butConsumer,@NotNull InputStream inputStream) throws IOException;
}
