package com.studymind.repository;

import com.studymind.model.Deck;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeckRepository extends MongoRepository<Deck, String> {

    Optional<Deck> findByDocumentIdAndIsLatestTrue(String documentId);

    Optional<Deck> findByIdAndUserId(String id, String userId);

    void deleteByDocumentId(String documentId);
}
