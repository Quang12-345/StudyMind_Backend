package com.studymind.repository;

import com.studymind.model.Flashcard;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FlashcardRepository extends MongoRepository<Flashcard, String> {

    List<Flashcard> findByDeckIdOrderByOrderAsc(String deckId);

    Optional<Flashcard> findByIdAndUserId(String id, String userId);

    void deleteByDeckId(String deckId);

    void deleteByDocumentId(String documentId);
}
