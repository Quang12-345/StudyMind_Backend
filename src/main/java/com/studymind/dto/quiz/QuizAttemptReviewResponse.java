package com.studymind.dto.quiz;

import java.time.Instant;
import java.util.List;

public record QuizAttemptReviewResponse(
        String id,
        String quizId,
        Integer score,
        Integer totalQuestions,
        List<QuizQuestionReview> questions,
        Instant completedAt
) {}
