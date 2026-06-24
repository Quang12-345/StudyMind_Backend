package com.studymind.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "decks")
@CompoundIndex(name = "document_latest_idx", def = "{'documentId': 1, 'isLatest': 1}")
public class Deck extends BaseDocument {

    @Indexed
    private String documentId;

    @Indexed
    private String userId;

    private String title;
    private Integer cardCount = 0;
    private Integer version = 1;
    private Boolean isLatest = true;
}
