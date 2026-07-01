package com.studymind.dto.quiz;

import com.studymind.model.QuizAttempt;
import java.time.Instant;
import java.util.List;

public record QuizAttemptResponse(
        String id,
        String quizId,
        String documentId,
        Integer score,
        Integer totalQuestions,
        Integer timeSpentSeconds,
        List<QuizAnswerRecordResponse> answers,
        Instant completedAt
) {
    public static QuizAttemptResponse from(QuizAttempt attempt) {
        return new QuizAttemptResponse(
                attempt.getId(),
                attempt.getQuizId(),
                attempt.getDocumentId(),
                attempt.getScore(),
                attempt.getTotalQuestions(),
                attempt.getTimeSpentSeconds(),
                attempt.getAnswers().stream().map(QuizAnswerRecordResponse::from).toList(),
                attempt.getCompletedAt()
        );
    }
}
