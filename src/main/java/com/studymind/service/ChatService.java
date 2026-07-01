package com.studymind.service;

import com.studymind.dto.chat.ChatMessageResponse;
import com.studymind.dto.chat.ChatSessionResponse;
import com.studymind.dto.chat.CreateChatSessionRequest;
import com.studymind.dto.chat.SendMessageRequest;
import com.studymind.dto.chat.SendMessageResponse;
import com.studymind.dto.chat.UpdateChatSessionRequest;
import com.studymind.exception.BadRequestException;
import com.studymind.exception.ResourceNotFoundException;
import com.studymind.model.ChatMessage;
import com.studymind.model.ChatSession;
import com.studymind.model.DocumentChunk;
import com.studymind.model.StudyDocument;
import com.studymind.model.enums.ChatRole;
import com.studymind.model.enums.ProcessingStepStatus;
import com.studymind.repository.ChatMessageRepository;
import com.studymind.repository.ChatSessionRepository;
import com.studymind.repository.DocumentChunkRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final DocumentChunkRepository documentChunkRepository;
    private final DocumentAccessService documentAccessService;

    public ChatService(
            ChatSessionRepository chatSessionRepository,
            ChatMessageRepository chatMessageRepository,
            DocumentChunkRepository documentChunkRepository,
            DocumentAccessService documentAccessService
    ) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.documentChunkRepository = documentChunkRepository;
        this.documentAccessService = documentAccessService;
    }

    public ChatSessionResponse createSession(String documentId, String userId, CreateChatSessionRequest request) {
        StudyDocument document = documentAccessService.requireOwnedDocument(documentId, userId);
        ensureIndexed(document);

        ChatSession session = new ChatSession();
        session.setDocumentId(documentId);
        session.setUserId(userId);
        session.setTitle(request.title() != null && !request.title().isBlank()
                ? request.title().trim()
                : "Chat — " + document.getTitle());
        return ChatSessionResponse.from(chatSessionRepository.save(session));
    }

    public List<ChatSessionResponse> listSessions(String documentId, String userId) {
        documentAccessService.requireOwnedDocument(documentId, userId);
        return chatSessionRepository.findByDocumentIdAndUserIdOrderByUpdatedAtDesc(documentId, userId).stream()
                .map(ChatSessionResponse::from)
                .toList();
    }

    public List<ChatMessageResponse> listMessages(String sessionId, String userId) {
        requireOwnedSession(sessionId, userId);
        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId).stream()
                .map(ChatMessageResponse::from)
                .toList();
    }

    public SendMessageResponse sendMessage(String sessionId, String userId, SendMessageRequest request) {
        ChatSession session = requireOwnedSession(sessionId, userId);
        StudyDocument document = documentAccessService.requireOwnedDocument(session.getDocumentId(), userId);
        ensureIndexed(document);

        ChatMessage userMessage = saveMessage(session, userId, ChatRole.USER, request.content().trim(), List.of());

        List<DocumentChunk> chunks = documentChunkRepository.findByDocumentIdOrderByChunkIndexAsc(document.getId());
        List<String> referencedChunkIds = findRelevantChunks(chunks, request.content());
        String context = buildContext(chunks, referencedChunkIds);
        String answer = buildStubAnswer(document.getTitle(), request.content(), context);

        ChatMessage assistantMessage = saveMessage(session, userId, ChatRole.ASSISTANT, answer, referencedChunkIds);

        return new SendMessageResponse(
                ChatMessageResponse.from(userMessage),
                ChatMessageResponse.from(assistantMessage)
        );
    }

    public ChatSessionResponse updateSession(String sessionId, String userId, UpdateChatSessionRequest request) {
        ChatSession session = requireOwnedSession(sessionId, userId);
        session.setTitle(request.title().trim());
        return ChatSessionResponse.from(chatSessionRepository.save(session));
    }

    public void deleteSession(String sessionId, String userId) {
        requireOwnedSession(sessionId, userId);
        chatMessageRepository.deleteBySessionId(sessionId);
        chatSessionRepository.deleteById(sessionId);
    }

    private ChatSession requireOwnedSession(String sessionId, String userId) {
        return chatSessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found"));
    }

    private void ensureIndexed(StudyDocument document) {
        if (document.getProcessingSteps().getIndexing() != ProcessingStepStatus.DONE) {
            throw new BadRequestException("Document must be processed before using chat");
        }
    }

    private ChatMessage saveMessage(
            ChatSession session,
            String userId,
            ChatRole role,
            String content,
            List<String> referencedChunkIds
    ) {
        ChatMessage message = new ChatMessage();
        message.setSessionId(session.getId());
        message.setDocumentId(session.getDocumentId());
        message.setUserId(userId);
        message.setRole(role);
        message.setContent(content);
        message.setReferencedChunkIds(new ArrayList<>(referencedChunkIds));
        message.setTokenCount(content.split("\\s+").length);
        return chatMessageRepository.save(message);
    }

    private List<String> findRelevantChunks(List<DocumentChunk> chunks, String query) {
        if (chunks.isEmpty()) {
            return List.of();
        }
        String lowerQuery = query.toLowerCase();
        return chunks.stream()
                .filter(chunk -> chunk.getContent().toLowerCase().contains(lowerQuery)
                        || lowerQuery.length() > 3 && chunk.getContent().toLowerCase().contains(lowerQuery.substring(0, 3)))
                .limit(3)
                .map(DocumentChunk::getId)
                .toList();
    }

    private String buildContext(List<DocumentChunk> chunks, List<String> chunkIds) {
        if (chunkIds.isEmpty()) {
            return chunks.stream()
                    .sorted(Comparator.comparing(DocumentChunk::getChunkIndex))
                    .limit(2)
                    .map(DocumentChunk::getContent)
                    .reduce((a, b) -> a + "\n\n" + b)
                    .orElse("No indexed content available.");
        }
        return chunks.stream()
                .filter(chunk -> chunkIds.contains(chunk.getId()))
                .map(DocumentChunk::getContent)
                .reduce((a, b) -> a + "\n\n" + b)
                .orElse("");
    }

    private String buildStubAnswer(String title, String question, String context) {
        return "Based on \"" + title + "\", here is a response to your question:\n\n"
                + "Q: " + question + "\n\n"
                + "Context excerpt:\n" + truncate(context, 600) + "\n\n"
                + "(Stub AI response — replace with real LLM integration.)";
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }
}
