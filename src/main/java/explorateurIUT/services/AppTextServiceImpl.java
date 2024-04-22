package explorateurIUT.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import explorateurIUT.model.AppTextRepository;
import jakarta.validation.constraints.NotBlank;

public class AppTextServiceImpl implements AppTextService {
    private final static String DEFAULT_LANGUAGE = "fr";
    private final AppTextRepository textRepo;

    @Autowired
    public AppTextServiceImpl(AppTextRepository appTextRepository){
        textRepo = appTextRepository;
    }
    @Override
    public Map<String, String> getAppTextsByCode(@NotBlank String language) {
        this.textRepo.streamByLanguage(language);
        return null;
    }

    @Override
    public Map<String, String> getDefaultAppTextsByCode() {
        return getAppTextsByCode(DEFAULT_LANGUAGE);
    }
    
}
