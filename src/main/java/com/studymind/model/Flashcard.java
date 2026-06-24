package com.studymind.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "flashcards")
@CompoundIndex(name = "deck_order_idx", def = "{'deckId': 1, 'order': 1}")
public class Flashcard extends BaseDocument {

    @Indexed
    private String deckId;

    @Indexed
    private String documentId;

    @Indexed
    private String userId;

    private String front;
    private String back;
    private Integer order;
    private Integer sourcePage;
    private String sourceChunkId;
    private Boolean isKnown = false;
    private Integer reviewCount = 0;
}
