package explorateurIUT.services;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;

public interface DataUploadService {
    public void uploadData(@NotNull MultipartFile dataExcelFile) throws ConstraintViolationException;
}
