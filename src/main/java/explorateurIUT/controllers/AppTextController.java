package explorateurIUT.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import explorateurIUT.services.AppTextService;

@RestController
@RequestMapping("/api/v1/texte")
public class AppTextController {
    private final AppTextService appTextSvc;

    @Autowired
    public AppTextController(AppTextService appTextService){
        this.appTextSvc = appTextService;
    }

    public Map<String, String> getText(@RequestParam(name="lang", defaultValue = "fr", required = false) String language) {
        return this.appTextSvc.getAppTextsByCode(language);
    }
}
