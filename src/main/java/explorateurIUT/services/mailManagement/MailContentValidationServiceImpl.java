package explorateurIUT.services.mailManagement;

import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.PostConstruct;
import jakarta.validation.ValidationException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@Validated
public class MailContentValidationServiceImpl implements MailContentValidationService {

    private final MailSendingProperties mailSendingProp;

    private Cleaner cleaner;

    @Autowired
    public MailContentValidationServiceImpl(MailSendingProperties mailSendingProp) {
        this.mailSendingProp = mailSendingProp;
    }

    @PostConstruct
    public void init() {
        this.cleaner = new Cleaner(Safelist.none());
    }

    @Override
    public boolean isValid(String mailContent) {
        return this.cleaner.isValidBodyHtml(mailContent);
    }

    @Override
    public boolean isValid(List<MailRequestAttachement> attachements) throws ValidationException {
        if (attachements == null || attachements.isEmpty()) {
            return true;
        }
        // Check max number of attachement
        if (attachements.size() > this.mailSendingProp.getMaxAttachementNumber()) {
            return false;
        }
        // Check each attachement size and compute total size;
        final long maxAttachementSizeBytes = ((long) this.mailSendingProp.getMaxAttachementSizeMB()) * 1000000;
        long totalAttachementSize = 0;
        for (MailRequestAttachement attachement : attachements) {
            final long sizeInBytes = attachement.file().getSize();
            if (sizeInBytes > maxAttachementSizeBytes) {
                return false;
            }
            totalAttachementSize += sizeInBytes;
        }
        // Check total size
        final long maxAttachementsTotalSize = ((long) this.mailSendingProp.getMaxAttachementsTotalSizeMB()) * 1000000;
        if (totalAttachementSize > maxAttachementsTotalSize) {
            return false;
        }

        return true;
    }
}
