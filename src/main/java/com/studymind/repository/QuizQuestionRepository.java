package com.studymind.repository;

import com.studymind.model.QuizQuestion;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuizQuestionRepository extends MongoRepository<QuizQuestion, String> {

    List<QuizQuestion> findByQuizIdOrderByOrderAsc(String quizId);

    void deleteByQuizId(String quizId);

    void deleteByDocumentId(String documentId);
}
