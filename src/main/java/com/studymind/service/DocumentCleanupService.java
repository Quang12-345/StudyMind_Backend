package com.studymind.service;

import com.studymind.repository.AiJobRepository;
import com.studymind.repository.ChatMessageRepository;
import com.studymind.repository.ChatSessionRepository;
import com.studymind.repository.DocumentChunkRepository;
import com.studymind.repository.DocumentTextRepository;
import com.studymind.repository.FlashcardRepository;
import com.studymind.repository.QuizAttemptRepository;
import com.studymind.repository.QuizQuestionRepository;
import com.studymind.repository.QuizRepository;
import com.studymind.repository.SummaryRepository;
import com.studymind.repository.DeckRepository;
import org.springframework.stereotype.Service;

@Service
public class DocumentCleanupService {

    private final DocumentTextRepository documentTextRepository;
    private final DocumentChunkRepository documentChunkRepository;
    private final SummaryRepository summaryRepository;
    private final DeckRepository deckRepository;
    private final FlashcardRepository flashcardRepository;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AiJobRepository aiJobRepository;

    public DocumentCleanupService(
            DocumentTextRepository documentTextRepository,
            DocumentChunkRepository documentChunkRepository,
            SummaryRepository summaryRepository,
            DeckRepository deckRepository,
            FlashcardRepository flashcardRepository,
            QuizRepository quizRepository,
            QuizQuestionRepository quizQuestionRepository,
            QuizAttemptRepository quizAttemptRepository,
            ChatSessionRepository chatSessionRepository,
            ChatMessageRepository chatMessageRepository,
            AiJobRepository aiJobRepository
    ) {
        this.documentTextRepository = documentTextRepository;
        this.documentChunkRepository = documentChunkRepository;
        this.summaryRepository = summaryRepository;
        this.deckRepository = deckRepository;
        this.flashcardRepository = flashcardRepository;
        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.aiJobRepository = aiJobRepository;
    }

    public void deleteRelatedData(String documentId) {
        chatMessageRepository.deleteByDocumentId(documentId);
        chatSessionRepository.deleteByDocumentId(documentId);
        quizAttemptRepository.deleteByDocumentId(documentId);
        quizQuestionRepository.deleteByDocumentId(documentId);
        quizRepository.deleteByDocumentId(documentId);
        flashcardRepository.deleteByDocumentId(documentId);
        deckRepository.deleteByDocumentId(documentId);
        summaryRepository.deleteByDocumentId(documentId);
        documentChunkRepository.deleteByDocumentId(documentId);
        documentTextRepository.deleteByDocumentId(documentId);
        aiJobRepository.deleteByDocumentId(documentId);
    }
}
