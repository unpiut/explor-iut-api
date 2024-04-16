package explorateurIUT.services.mailManagement;

import org.springframework.stereotype.Service;

import jakarta.validation.ValidationException;

@Service
public class MailContentForgerServiceImpl implements MailContentForgerService {

    private void createExtraInformation(MailSendingRequest mailSendingRequest, StringBuilder finalBody){
        finalBody.append("Identité : ")
        .append(mailSendingRequest.getContactIdentity())
        .append("\n")
        .append("Nom entreprise : ")
        .append(mailSendingRequest.getContactCompany())
        .append("\n")
        .append("Fonction dans l'entreprise : ")
        .append(mailSendingRequest.getContactFunction())
        .append("\n")
        .append("Mail du contact : ")
        .append(mailSendingRequest.getContactMail());
    }
    @Override
    public String createBody(MailSendingRequest mailSendingRequest) throws ValidationException{
        final StringBuilder finalBody = new StringBuilder(mailSendingRequest.getBody());
        finalBody.append("\n").append("-".repeat(10)).append("\n"); //Add a separator between the body and the informations
        this.createExtraInformation(mailSendingRequest, finalBody);
        return finalBody.toString();
    }
}
