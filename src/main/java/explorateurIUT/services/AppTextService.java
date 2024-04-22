package explorateurIUT.services;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;

public interface AppTextService {
    Map<String, String> getAppTextsByCode(@NotBlank String language);
    Map<String, String> getDefaultAppTextsByCode();
}
