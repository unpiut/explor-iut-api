package explorateurIUT.services;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

@Transactional
public interface ExcelChangeService {
    public void changeExcel(InputStream dataExcel) throws IOException;
}
