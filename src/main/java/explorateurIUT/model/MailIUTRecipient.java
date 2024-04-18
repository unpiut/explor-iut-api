package explorateurIUT.model;

import java.util.List;

public class MailIUTRecipient {
    private String mailAddress;
    private List<String> departementCodes;

    public MailIUTRecipient(){

    };

    public MailIUTRecipient(String mailAddress, List<String> departementCodes) {
        this.mailAddress = mailAddress;
        this.departementCodes = departementCodes;

    }

    public String getMailAddress() {
        return mailAddress;
    }

    protected void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public List<String> getDepartementCodes() {
        return departementCodes;
    }

    protected void setDepartementCodes(List<String> departementCodes) {
        this.departementCodes = departementCodes;
    }
    
}
