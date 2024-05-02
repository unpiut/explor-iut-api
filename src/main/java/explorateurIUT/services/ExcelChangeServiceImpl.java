package explorateurIUT.services;

import explorateurIUT.excelImport.AppDataProperties;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExcelChangeServiceImpl implements ExcelChangeService {

    private static final Log LOG = LogFactory.getLog(ExcelChangeServiceImpl.class);

    private final AppDataProperties appDataProperties;

    @Autowired
    public ExcelChangeServiceImpl(AppDataProperties appDataProperties) {
        this.appDataProperties = appDataProperties;
    }

    @Override
    public ExcelChangeSession getChangeExcelSession() throws IOException {
        return new ChangeSessionImpl();
    }

    public class ChangeSessionImpl implements ExcelChangeSession {

        private Path backupFile;
        private boolean backupCreated = false;
        private boolean fileChanged = false;

        @Override
        public void applyChange(InputStream dataExcel) throws IOException, SecurityException {
            this.backupFile = Files.createTempFile("exploriut-bckup-data", ".xlxs");
            Files.copy(Path.of(appDataProperties.getFilePath()), this.backupFile, StandardCopyOption.REPLACE_EXISTING);
            this.backupCreated = true;
            Files.copy(dataExcel, Path.of(appDataProperties.getFilePath()), StandardCopyOption.REPLACE_EXISTING);
            this.fileChanged = true;
        }

        @Override
        public void commit() {
            if (this.backupCreated) {
                try {
                    Files.deleteIfExists(this.backupFile);
                    this.backupCreated = false;
                } catch (IOException ex) {
                    LOG.warn("Unable to remove backup file");
                }
            }
        }

        @Override
        public void rollback() throws IOException, SecurityException {
            if (this.backupCreated && this.fileChanged) {
                try {
                    Files.move(this.backupFile, Path.of(appDataProperties.getFilePath()), StandardCopyOption.ATOMIC_MOVE);
                } catch (AtomicMoveNotSupportedException error) {
                    LOG.debug("The atomic move failed.");
                    Files.move(this.backupFile, Path.of(appDataProperties.getFilePath()), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }

    }

}
