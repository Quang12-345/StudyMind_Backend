package com.studymind.service;

import com.studymind.dto.flashcard.CreateFlashcardRequest;
import com.studymind.dto.flashcard.DeckDetailResponse;
import com.studymind.dto.flashcard.FlashcardResponse;
import com.studymind.dto.flashcard.UpdateFlashcardRequest;
import com.studymind.exception.BadRequestException;
import com.studymind.exception.ResourceNotFoundException;
import com.studymind.model.Deck;
import com.studymind.model.Flashcard;
import com.studymind.model.StudyDocument;
import com.studymind.repository.DeckRepository;
import com.studymind.repository.FlashcardRepository;
import com.studymind.repository.StudyDocumentRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FlashcardService {

    private final DeckRepository deckRepository;
    private final FlashcardRepository flashcardRepository;
    private final DocumentAccessService documentAccessService;
    private final StudyDocumentRepository documentRepository;

    public FlashcardService(
            DeckRepository deckRepository,
            FlashcardRepository flashcardRepository,
            DocumentAccessService documentAccessService,
            StudyDocumentRepository documentRepository
    ) {
        this.deckRepository = deckRepository;
        this.flashcardRepository = flashcardRepository;
        this.documentAccessService = documentAccessService;
        this.documentRepository = documentRepository;
    }

    public DeckDetailResponse getDeckByDocumentId(String documentId, String userId) {
        documentAccessService.requireOwnedDocument(documentId, userId);
        Deck deck = deckRepository.findByDocumentIdAndIsLatestTrue(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard deck not found for this document"));
        List<Flashcard> cards = flashcardRepository.findByDeckIdOrderByOrderAsc(deck.getId());
        return DeckDetailResponse.from(deck, cards);
    }

    public FlashcardResponse createCard(String documentId, String userId, CreateFlashcardRequest request) {
        StudyDocument document = documentAccessService.requireOwnedDocument(documentId, userId);
        Deck deck = deckRepository.findByDocumentIdAndIsLatestTrue(documentId)
                .orElseGet(() -> createEmptyDeck(document));

        List<Flashcard> existing = flashcardRepository.findByDeckIdOrderByOrderAsc(deck.getId());
        int nextOrder = existing.isEmpty() ? 1 : existing.get(existing.size() - 1).getOrder() + 1;

        Flashcard card = new Flashcard();
        card.setDeckId(deck.getId());
        card.setDocumentId(documentId);
        card.setUserId(userId);
        card.setFront(request.front().trim());
        card.setBack(request.back().trim());
        card.setOrder(nextOrder);
        card.setSourcePage(request.sourcePage());
        Flashcard saved = flashcardRepository.save(card);

        deck.setCardCount(existing.size() + 1);
        deckRepository.save(deck);
        document.setDeckId(deck.getId());
        documentRepository.save(document);
        return FlashcardResponse.from(saved);
    }

    public FlashcardResponse updateCard(String cardId, String userId, UpdateFlashcardRequest request) {
        Flashcard card = flashcardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard not found"));

        if (request.front() != null) {
            if (request.front().isBlank()) {
                throw new BadRequestException("Front text cannot be blank");
            }
            card.setFront(request.front().trim());
        }
        if (request.back() != null) {
            if (request.back().isBlank()) {
                throw new BadRequestException("Back text cannot be blank");
            }
            card.setBack(request.back().trim());
        }
        if (request.isKnown() != null) {
            card.setIsKnown(request.isKnown());
            if (Boolean.TRUE.equals(request.isKnown())) {
                card.setReviewCount(card.getReviewCount() + 1);
            }
        }
        return FlashcardResponse.from(flashcardRepository.save(card));
    }

    public void deleteCard(String cardId, String userId) {
        Flashcard card = flashcardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard not found"));
        flashcardRepository.delete(card);

        deckRepository.findById(card.getDeckId()).ifPresent(deck -> {
            long count = flashcardRepository.findByDeckIdOrderByOrderAsc(deck.getId()).size();
            deck.setCardCount((int) count);
            deckRepository.save(deck);
        });
    }

    private Deck createEmptyDeck(StudyDocument document) {
        Deck deck = new Deck();
        deck.setDocumentId(document.getId());
        deck.setUserId(document.getUserId());
        deck.setTitle("Flashcards — " + document.getTitle());
        deck.setCardCount(0);
        return deckRepository.save(deck);
    }
}
