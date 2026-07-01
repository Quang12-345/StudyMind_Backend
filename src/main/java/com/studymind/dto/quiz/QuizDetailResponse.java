package com.studymind.dto.quiz;

import com.studymind.model.Quiz;
import com.studymind.model.QuizQuestion;
import java.time.Instant;
import java.util.List;

public record QuizDetailResponse(
        String id,
        String documentId,
        String title,
        Integer questionCount,
        Integer timeLimitMinutes,
        List<QuizQuestionForAttempt> questions,
        Instant createdAt
) {
    public static QuizDetailResponse from(Quiz quiz, List<QuizQuestion> questions) {
        return new QuizDetailResponse(
                quiz.getId(),
                quiz.getDocumentId(),
                quiz.getTitle(),
                quiz.getQuestionCount(),
                quiz.getTimeLimitMinutes(),
                questions.stream().map(QuizQuestionForAttempt::from).toList(),
                quiz.getCreatedAt()
        );
    }
}
