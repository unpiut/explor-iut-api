package explorateurIUT.services.mailManagement;

public class MailContentValidationServiceImpl implements MailContentValidationService{
    public boolean sanitizer(String[] informations,String body, String subject){
        try{
            for(int i=0;i<informations.length;i++){
                //sanitize(informations[i]);
            }
            //sanitize(body);
            //sanitize(subject);
        } catch(Error err) {
            throw err;
        }
        return true;
    }
}
