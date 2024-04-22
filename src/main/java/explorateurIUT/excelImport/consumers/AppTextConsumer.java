package explorateurIUT.excelImport.consumers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import java.util.function.Consumer;
import explorateurIUT.excelImport.model.ExcelAppText;
import explorateurIUT.model.AppText;
public class AppTextConsumer implements Consumer<ExcelAppText> {
    private static final Log LOG = LogFactory.getLog(BUTConsumer.class);

    private final MongoTemplate mongoTemplate;

    public AppTextConsumer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void accept(ExcelAppText excelText) {
        //Create an instance of AppText then all instance of parcours
        LOG.debug("Save App Text " + excelText.getCode());
        mongoTemplate.save(textFromExcel(excelText));
    }

    private static AppText textFromExcel(ExcelAppText text) {
        return new AppText(text.getCode(), text.getContent());
    }
}
