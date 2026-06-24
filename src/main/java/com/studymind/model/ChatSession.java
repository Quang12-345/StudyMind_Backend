package com.studymind.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "chat_sessions")
@CompoundIndex(name = "user_document_idx", def = "{'userId': 1, 'documentId': 1, 'updatedAt': -1}")
public class ChatSession extends BaseDocument {

    @Indexed
    private String documentId;

    @Indexed
    private String userId;

    private String title;
}
