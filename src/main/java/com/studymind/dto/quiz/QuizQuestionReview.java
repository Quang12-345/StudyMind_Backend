package com.studymind.dto.quiz;

import java.util.List;

public record QuizQuestionReview(
        String id,
        String question,
        List<String> options,
        Integer correctIndex,
        String correctAnswer,
        String explanation,
        Integer selectedIndex,
        String shortAnswer,
        boolean correct
) {}
