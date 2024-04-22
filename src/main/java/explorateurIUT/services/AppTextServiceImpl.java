package explorateurIUT.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import explorateurIUT.model.AppText;
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
        Map<String, String> dico = new HashMap<>();
        List<AppText> listText = this.textRepo.streamByLanguage(language).toList();
        for (AppText appText : listText) {
            dico.put(appText.getCode(), appText.getContent());
        }
        return dico;
    }

    @Override
    public Map<String, String> getDefaultAppTextsByCode() {
        return getAppTextsByCode(DEFAULT_LANGUAGE);
    }
    
}
