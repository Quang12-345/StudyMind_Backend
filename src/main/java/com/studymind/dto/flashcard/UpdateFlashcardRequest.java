package com.studymind.dto.flashcard;

import jakarta.validation.constraints.Size;

public record UpdateFlashcardRequest(
        @Size(max = 2000) String front,
        @Size(max = 2000) String back,
        Boolean isKnown
) {}
