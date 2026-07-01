package com.studymind.dto.chat;

import com.studymind.model.ChatSession;
import java.time.Instant;

public record ChatSessionResponse(
        String id,
        String documentId,
        String title,
        Instant createdAt,
        Instant updatedAt
) {
    public static ChatSessionResponse from(ChatSession session) {
        return new ChatSessionResponse(
                session.getId(),
                session.getDocumentId(),
                session.getTitle(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }
}
