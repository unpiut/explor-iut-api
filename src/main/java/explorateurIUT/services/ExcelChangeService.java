package explorateurIUT.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface ExcelChangeService {
    public void changeExcel(MultipartFile file) throws IOException;
}
