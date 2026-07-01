package com.studymind.repository;

import com.studymind.model.Quiz;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuizRepository extends MongoRepository<Quiz, String> {

    Optional<Quiz> findByDocumentIdAndIsLatestTrue(String documentId);

    Optional<Quiz> findByIdAndUserId(String id, String userId);

    void deleteByDocumentId(String documentId);
}
