package com.studymind.controller;

import com.studymind.dto.ApiResponse;
import com.studymind.dto.flashcard.CreateFlashcardRequest;
import com.studymind.dto.flashcard.DeckDetailResponse;
import com.studymind.dto.flashcard.FlashcardResponse;
import com.studymind.dto.flashcard.UpdateFlashcardRequest;
import com.studymind.security.UserPrincipal;
import com.studymind.service.FlashcardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Flashcards", description = "Quản lý flashcard")
@SecurityRequirement(name = "Bearer Authentication")
public class FlashcardController {

    private final FlashcardService flashcardService;

    public FlashcardController(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }

    @GetMapping("/api/v1/documents/{documentId}/deck")
    @Operation(summary = "Lấy deck flashcard của document")
    public ApiResponse<DeckDetailResponse> getDeck(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String documentId
    ) {
        return ApiResponse.ok(flashcardService.getDeckByDocumentId(documentId, principal.getId()));
    }

    @PostMapping("/api/v1/documents/{documentId}/deck/cards")
    @Operation(summary = "Thêm flashcard thủ công")
    public ApiResponse<FlashcardResponse> createCard(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String documentId,
            @Valid @RequestBody CreateFlashcardRequest request
    ) {
        return ApiResponse.ok("Flashcard created", flashcardService.createCard(documentId, principal.getId(), request));
    }

    @PatchMapping("/api/v1/flashcards/{cardId}")
    @Operation(summary = "Cập nhật flashcard")
    public ApiResponse<FlashcardResponse> updateCard(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String cardId,
            @Valid @RequestBody UpdateFlashcardRequest request
    ) {
        return ApiResponse.ok(flashcardService.updateCard(cardId, principal.getId(), request));
    }

    @DeleteMapping("/api/v1/flashcards/{cardId}")
    @Operation(summary = "Xóa flashcard")
    public ApiResponse<Void> deleteCard(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String cardId
    ) {
        flashcardService.deleteCard(cardId, principal.getId());
        return ApiResponse.ok("Flashcard deleted", null);
    }
}
