package explorateurIUT.model;

import explorateurIUT.model.converters.PendingMailIUTRecipientDeptCodesConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import java.util.List;
import java.util.Objects;

@Entity
public class PendingMailIUTRecipient {

    @EmbeddedId
    private PendingMailIUTRecipientPK id;

    @MapsId("mailId")
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false)
    private PendingMail mail;

    @Convert(converter = PendingMailIUTRecipientDeptCodesConverter.class)
    private List<String> departementCodes;

    protected PendingMailIUTRecipient() {

    }

    public PendingMailIUTRecipient(PendingMail mail, String mailAddress, List<String> departementCodes) {
        this.id = new PendingMailIUTRecipientPK(mail.getId(), mailAddress);
        this.departementCodes = departementCodes;
    }

    public PendingMailIUTRecipientPK getId() {
        return id;
    }

    protected void setId(PendingMailIUTRecipientPK id) {
        this.id = id;
    }

    public PendingMail getMail() {
        return mail;
    }

    protected void setMail(PendingMail mail) {
        this.mail = mail;
    }

    public String getMailAddress() {
        return this.id.getMailAddress();
    }

    protected void setMailAddress(String mailAddress) {
        this.id.setMailAddress(mailAddress);
    }

    public List<String> getDepartementCodes() {
        return departementCodes;
    }

    protected void setDepartementCodes(List<String> departementCodes) {
        this.departementCodes = departementCodes;
    }

    @Override
    public int hashCode() {
        if (this.id == null) {
            return super.hashCode();
        }
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PendingMailIUTRecipient other = (PendingMailIUTRecipient) obj;
        if (this.id == null || other.id == null) {
            return false;
        }
        return Objects.equals(this.id, other.id);
    }

}
