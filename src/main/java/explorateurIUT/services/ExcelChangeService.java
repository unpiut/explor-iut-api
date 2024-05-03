package explorateurIUT.services;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.transaction.annotation.Transactional;

public interface ExcelChangeService {
    ExcelChangeSession getChangeExcelSession() throws IOException;

    interface ExcelChangeSession {

        void applyChange(InputStream dataExcel) throws IOException, SecurityException;

        void commit();

        void rollback() throws IOException, SecurityException;
    }
}