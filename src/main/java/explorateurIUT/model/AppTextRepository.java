package explorateurIUT.model;

import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppTextRepository extends MongoRepository<AppText, String> {
    Stream<AppText> streamByLanguage(String language);
}
