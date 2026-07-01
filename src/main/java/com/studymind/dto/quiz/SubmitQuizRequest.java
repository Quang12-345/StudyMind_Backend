package com.studymind.dto.quiz;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record SubmitQuizRequest(
        @NotEmpty List<@Valid QuizAnswerSubmission> answers,
        @Min(0) Integer timeSpentSeconds
) {}
