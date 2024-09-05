package explorateurIUT.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import explorateurIUT.services.AppTextService;
import java.util.NoSuchElementException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/textes")
public class AppTextController {

    private final AppTextService appTextSvc;

    @Autowired
    public AppTextController(AppTextService appTextService) {
        this.appTextSvc = appTextService;
    }

    @GetMapping
    public Map<String, String> getAllTexts(@RequestParam(name = "lang", defaultValue = "fr", required = false) String language) {
        return this.appTextSvc.getAppTextsByCode(language);
    }

    @GetMapping("{textId:[a-zA-Z0-9_\\-]{2,100}}")
    public String getText(
            @PathVariable String textId,
            @RequestParam(name = "lang", defaultValue = "fr", required = false) String language) {
        final String text = this.appTextSvc.getAppTextsByCode(language).get(textId);
        if (text == null) {
            throw new NoSuchElementException("Unknown text");
        }
        return text;
    }
}
