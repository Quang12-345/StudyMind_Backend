package com.studymind.repository;

import com.studymind.model.QuizAttempt;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuizAttemptRepository extends MongoRepository<QuizAttempt, String> {

    List<QuizAttempt> findByQuizIdAndUserIdOrderByCompletedAtDesc(String quizId, String userId);

    Optional<QuizAttempt> findByIdAndUserId(String id, String userId);

    void deleteByDocumentId(String documentId);
}
