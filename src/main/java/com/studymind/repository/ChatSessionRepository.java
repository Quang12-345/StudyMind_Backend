package com.studymind.repository;

import com.studymind.model.ChatSession;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatSessionRepository extends MongoRepository<ChatSession, String> {

    List<ChatSession> findByDocumentIdAndUserIdOrderByUpdatedAtDesc(String documentId, String userId);

    Optional<ChatSession> findByIdAndUserId(String id, String userId);

    void deleteByDocumentId(String documentId);
}
