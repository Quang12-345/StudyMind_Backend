package com.studymind.dto.flashcard;

import com.studymind.model.Deck;
import com.studymind.model.Flashcard;
import java.time.Instant;
import java.util.List;

public record DeckDetailResponse(
        String id,
        String documentId,
        String title,
        Integer cardCount,
        Integer version,
        List<FlashcardResponse> cards,
        Instant createdAt
) {
    public static DeckDetailResponse from(Deck deck, List<Flashcard> cards) {
        return new DeckDetailResponse(
                deck.getId(),
                deck.getDocumentId(),
                deck.getTitle(),
                deck.getCardCount(),
                deck.getVersion(),
                cards.stream().map(FlashcardResponse::from).toList(),
                deck.getCreatedAt()
        );
    }
}
