package com.studymind.dto.flashcard;

import com.studymind.model.Flashcard;
import java.time.Instant;

public record FlashcardResponse(
        String id,
        String deckId,
        String documentId,
        String front,
        String back,
        Integer order,
        Integer sourcePage,
        Boolean isKnown,
        Integer reviewCount,
        Instant createdAt
) {
    public static FlashcardResponse from(Flashcard card) {
        return new FlashcardResponse(
                card.getId(),
                card.getDeckId(),
                card.getDocumentId(),
                card.getFront(),
                card.getBack(),
                card.getOrder(),
                card.getSourcePage(),
                card.getIsKnown(),
                card.getReviewCount(),
                card.getCreatedAt()
        );
    }
}
