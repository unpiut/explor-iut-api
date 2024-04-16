package explorateurIUT.services.mailManagement;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.ValidationException;

@Service
@Validated
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
    public String createBody(MailSendingRequest mailSendingRequest){
        final StringBuilder finalBody = new StringBuilder(mailSendingRequest.getBody());
        finalBody.append("\n")
        .append("Merci de transmettre cette demande au service compétent au sein de votre IUT et dans l’attente d’un retour rapide,")
        .append("\n").append("-".repeat(10)).append("\n"); //Add a separator between the body and the informations
        this.createExtraInformation(mailSendingRequest, finalBody);
        return finalBody.toString();
    }

    @Override
    public List<String> createListMail(MailSendingRequest mailSendingRequest){
        Collection<String> listId = mailSendingRequest.getIutIds();
        return null;
    }
}
