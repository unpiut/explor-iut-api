package explorateurIUT.services.mailManagement;

import org.springframework.stereotype.Service;

import jakarta.validation.ValidationException;

@Service
public class MailContentForgerServiceImpl implements MailContentForgerService {
    @Override
    public String bodyCreation(String[] informations, String body) throws ValidationException{
        String finalBody = body;
        finalBody.concat("\n --------"); //Add a separator between the body and the informations
        for(int i=0;i<informations.length;i++){
            finalBody.concat("\n"+informations[i]);
        }
        return finalBody;
    }
}
