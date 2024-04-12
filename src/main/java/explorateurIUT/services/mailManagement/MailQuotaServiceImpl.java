package explorateurIUT.services.mailManagement;

import org.springframework.stereotype.Service;

@Service
public class MailQuotaServiceImpl implements MailQuotaService{
    public boolean maxRequestAll(){
        //return findAllAfter1min < 10
        return true;
    }

    public boolean maxRequestIP(String clientIP) {
        //return findByIPAfter72h < 5
        return true;
    }
}
