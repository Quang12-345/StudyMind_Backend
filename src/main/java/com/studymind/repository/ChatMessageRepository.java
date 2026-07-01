package com.studymind.repository;

import com.studymind.model.ChatMessage;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    void deleteBySessionId(String sessionId);

    void deleteByDocumentId(String documentId);
}
