package com.studymind.dto.flashcard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFlashcardRequest(
        @NotBlank @Size(max = 2000) String front,
        @NotBlank @Size(max = 2000) String back,
        Integer sourcePage
) {}
