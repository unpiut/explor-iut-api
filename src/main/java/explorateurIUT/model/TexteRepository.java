package explorateurIUT.model;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TexteRepository extends MongoRepository<Texte, String> {
    Optional<Texte> findByCodeIgnoreCase(String code);
}
