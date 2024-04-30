package explorateurIUT.services;

import explorateurIUT.excelImport.AppDataProperties;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ExcelChangeServiceImpl implements ExcelChangeService {

    private static final Log LOG = LogFactory.getLog(ExcelChangeServiceImpl.class);
    
    private final AppDataProperties appDataProperties;

    @Autowired
    public ExcelChangeServiceImpl(AppDataProperties appDataProperties) {
        this.appDataProperties = appDataProperties;
    }

    @Override
    public void changeExcel(InputStream dataExcel) throws IOException {
        try {
            Files.copy(Path.of(this.appDataProperties.getFilePath()), Path.of("../../resources/backupFile"), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(dataExcel, Path.of(this.appDataProperties.getFilePath()), StandardCopyOption.REPLACE_EXISTING);
        } catch  {
            if (Files.exists(Path.of("../../resources/backupFile"))) {
                if (Files.isSameFile(Path.of(this.appDataProperties.getFilePath()), Path.of("../../resources/backupFile"))) {
                    try{
                        Files.move(Path.of(this.appDataProperties.getFilePath()), Path.of("../../resources/backupFile"), StandardCopyOption.ATOMIC_MOVE);
                    } catch(AtomicMoveNotSupportedException error){
                        LOG.debug("The atomic move failed.");
                        try{
                        Files.move(Path.of(this.appDataProperties.getFilePath()), Path.of("../../resources/backupFile"), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
            }
        }
    }
}
