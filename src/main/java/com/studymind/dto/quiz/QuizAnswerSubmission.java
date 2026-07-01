package com.studymind.dto.quiz;

import jakarta.validation.constraints.NotBlank;

public record QuizAnswerSubmission(
        @NotBlank String questionId,
        Integer selectedIndex,
        String shortAnswer
) {}
