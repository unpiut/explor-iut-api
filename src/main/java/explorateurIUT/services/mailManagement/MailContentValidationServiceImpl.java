package explorateurIUT.services.mailManagement;

import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.PostConstruct;

@Service
@Validated
public class MailContentValidationServiceImpl implements MailContentValidationService{
    private Cleaner cleaner;

    @PostConstruct
    public void init(){
        this.cleaner = new Cleaner(Safelist.none());
    }

    public boolean isValid(String mailContent){
        return this.cleaner.isValidBodyHtml(mailContent);
    }
}
