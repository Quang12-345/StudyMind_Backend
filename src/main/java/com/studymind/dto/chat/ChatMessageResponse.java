package com.studymind.dto.chat;

import com.studymind.model.ChatMessage;
import com.studymind.model.enums.ChatRole;
import java.time.Instant;
import java.util.List;

public record ChatMessageResponse(
        String id,
        String sessionId,
        ChatRole role,
        String content,
        List<String> referencedChunkIds,
        Instant createdAt
) {
    public static ChatMessageResponse from(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getSessionId(),
                message.getRole(),
                message.getContent(),
                message.getReferencedChunkIds(),
                message.getCreatedAt()
        );
    }
}
