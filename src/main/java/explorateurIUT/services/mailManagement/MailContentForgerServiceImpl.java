package explorateurIUT.services.mailManagement;

import explorateurIUT.model.IUTRepository;
import explorateurIUT.model.MailIUTRecipient;
import explorateurIUT.model.projections.DepartementCodesOfIUTId;
import explorateurIUT.model.projections.IUTMailOnly;
import explorateurIUT.model.DepartementRepository;
import explorateurIUT.services.AppTextService;
import explorateurIUT.services.CacheManagementServiceImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class MailContentForgerServiceImpl implements MailContentForgerService {

    private static final Log LOG = LogFactory.getLog(CacheManagementServiceImpl.class);

    private final static String NEW_LINE_MAIL = "\r\n";

    private final MailSendingProperties mailProp;

    private final DepartementRepository deptRepo;

    private final IUTRepository iutRepo;

    private final AppTextService appTextService;

    @Autowired
    public MailContentForgerServiceImpl(DepartementRepository deptRepo,
            IUTRepository iutRepo, MailSendingProperties mailProp, AppTextService appTextService) {
        this.deptRepo = deptRepo;
        this.iutRepo = iutRepo;
        this.mailProp = mailProp;
        this.appTextService = appTextService;
    }

    private void createExtraInformation(MailSendingRequest mailSendingRequest, StringBuilder generalBody) {
        final Map<String, String> mailTexts = this.appTextService.getDefaultMailTextsByCode();
        generalBody.append(mailTexts.getOrDefault("courielIUT_identiteLabel", "Identité :")).append(" ")
                .append(mailSendingRequest.contactIdentity())
                .append(NEW_LINE_MAIL)
                .append(mailTexts.getOrDefault("courielIUT_entrepriseLabel", "Nom entreprise :")).append(" ")
                .append(mailSendingRequest.contactCompany())
                .append(NEW_LINE_MAIL)
                .append(mailTexts.getOrDefault("courielIUT_fonctionLabel", "Fonction dans l'entreprise :")).append(" ")
                .append(mailSendingRequest.contactFunction())
                .append(NEW_LINE_MAIL)
                .append(mailTexts.getOrDefault("courielIUT_mailContactLabel", "Mail du contact :")).append(" ")
                .append(mailSendingRequest.contactMail());
    }

    @Override
    public String createGeneralBody(MailSendingRequest mailSendingRequest) {
        final Map<String, String> mailTexts = this.appTextService.getDefaultMailTextsByCode();
        final StringBuilder generalBody = new StringBuilder(mailSendingRequest.body());
        generalBody.append(NEW_LINE_MAIL.repeat(2))
                .append(NEW_LINE_MAIL).append("-".repeat(10)).append(NEW_LINE_MAIL)
                .append(mailTexts.getOrDefault("courielIUT_demandeTransmission", "Merci de transmettre cette demande au service compétent au sein de votre IUT et dans l’attente d’un retour rapide,"))
                .append(NEW_LINE_MAIL); //Add a separator between the body and the informations
        this.createExtraInformation(mailSendingRequest, generalBody);
        return generalBody.toString();
    }

    @Override
    public String createSpecificBody(String generalBody, List<String> codesDep) {
        final Map<String, String> mailTexts = this.appTextService.getDefaultMailTextsByCode();
        final StringBuilder finalBody = new StringBuilder(mailTexts.getOrDefault("courielIUT_enteteBonjour", "Bonjour ceci est un mail automatique de ExplorIUT,"));
        finalBody.append(NEW_LINE_MAIL).append(NEW_LINE_MAIL)
                .append(mailTexts.getOrDefault("courielIUT_deptConcernesLabel", "Les départements concernés par ce mail sont :"));
        for (String dep : codesDep) {
            finalBody.append(NEW_LINE_MAIL)
                    .append("- ").append(dep);
        }
        return finalBody.append(NEW_LINE_MAIL.repeat(2))
                .append(mailTexts.getOrDefault("courielIUT_pieceJointeAttentionLabel", "Attention, les possibles offres en pièces jointes peuvent être destinées à d'autres départements."))
                .append(NEW_LINE_MAIL)
                .append("-".repeat(10)).append(NEW_LINE_MAIL.repeat(2))
                .append(generalBody).toString();

    }

    @Override
    public List<MailIUTRecipient> createIUTMailingList(MailSendingRequest mailSendingRequest) {
        // Extract iutId related to deptId : the request ensure uniqueness of 
        LOG.debug("Creation de la liste des codes départements");
        List<DepartementCodesOfIUTId> codesDeptByIUT = this.deptRepo.streamIUTIdByIdIn(mailSendingRequest.deptIds())
                .toList();
        // Extract mail only of iut from Id. The request ensures uniqueness of mailId
        LOG.debug("Recuperation des ID d'IUT");
        List<String> iutIds = codesDeptByIUT.stream().map(DepartementCodesOfIUTId::getIut).toList();
        LOG.debug("Creation de la map et recuperation des mails");
        Map<String, String> mailsByIUTid = this.iutRepo.streamMailOnlyByIdInAndMelIsNotNull(iutIds)
                .collect(Collectors.toMap(IUTMailOnly::getId, IUTMailOnly::getMel));
        if (this.mailProp.getTestingMailAddress() != null && !this.mailProp.getTestingMailAddress().isEmpty()) {
            mailsByIUTid = new HashMap<>(mailsByIUTid);
            for (String iutId : iutIds) {
                mailsByIUTid.putIfAbsent(iutId, "unknown@mail.com");
            }
        }
        final Map<String, String> fMailsByIUTid = mailsByIUTid;
        LOG.debug("Renvoi des differents recipient");
        return codesDeptByIUT.stream()
                .filter((dep) -> fMailsByIUTid.containsKey(dep.getIut()))
                .map((dep) -> new MailIUTRecipient(fMailsByIUTid.get(dep.getIut()), dep.getCodes()))
                .toList();
    }

    @Override
    public String createConfirmationMailSubject() {
        return this.appTextService.getDefaultMailTextsByCode().getOrDefault("courielConfirmation_sujet", "ExplorerIUT - Confirmation de la recherche d'alternance");
    }

    @Override
    public String createConfirmationMailBody(String contactIdentity, String confirmationUrl) {
        final Map<String, String> mailTexts = this.appTextService.getDefaultMailTextsByCode();

        StringBuilder sb = new StringBuilder(mailTexts.getOrDefault("courielConfirmation_enteteBonjour", "Bonjour"));
        sb.append(" ")
                .append(contactIdentity).append(",").append(NEW_LINE_MAIL.repeat(2))
                .append(mailTexts.getOrDefault("courielConfirmation_textePrincipal", "Suite à votre demande de contact avec les IUT de France, vous devez confirmer votre envoi en cliquant sur ce lien :"))
                .append(NEW_LINE_MAIL)
                .append(confirmationUrl)
                .append(NEW_LINE_MAIL.repeat(2))
                .append(mailTexts.getOrDefault("courielConfirmation_formulePolitesse", "Cordialement,"))
                .append(NEW_LINE_MAIL)
                .append(mailTexts.getOrDefault("courielConfirmation_signature", "ExplorIUT"));
        return sb.toString();
    }

}
