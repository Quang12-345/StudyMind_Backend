package com.studymind.controller;

import com.studymind.dto.ApiResponse;
import com.studymind.dto.chat.ChatMessageResponse;
import com.studymind.dto.chat.ChatSessionResponse;
import com.studymind.dto.chat.CreateChatSessionRequest;
import com.studymind.dto.chat.SendMessageRequest;
import com.studymind.dto.chat.SendMessageResponse;
import com.studymind.dto.chat.UpdateChatSessionRequest;
import com.studymind.security.UserPrincipal;
import com.studymind.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Chat", description = "Chat AI theo document")
@SecurityRequirement(name = "Bearer Authentication")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/api/v1/documents/{documentId}/chat/sessions")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo chat session mới")
    public ApiResponse<ChatSessionResponse> createSession(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String documentId,
            @Valid @RequestBody(required = false) CreateChatSessionRequest request
    ) {
        CreateChatSessionRequest body = request != null ? request : new CreateChatSessionRequest(null);
        return ApiResponse.ok("Chat session created", chatService.createSession(documentId, principal.getId(), body));
    }

    @GetMapping("/api/v1/documents/{documentId}/chat/sessions")
    @Operation(summary = "Danh sách chat sessions")
    public ApiResponse<List<ChatSessionResponse>> listSessions(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String documentId
    ) {
        return ApiResponse.ok(chatService.listSessions(documentId, principal.getId()));
    }

    @GetMapping("/api/v1/chat/sessions/{sessionId}/messages")
    @Operation(summary = "Lịch sử tin nhắn")
    public ApiResponse<List<ChatMessageResponse>> listMessages(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String sessionId
    ) {
        return ApiResponse.ok(chatService.listMessages(sessionId, principal.getId()));
    }

    @PostMapping("/api/v1/chat/sessions/{sessionId}/messages")
    @Operation(summary = "Gửi tin nhắn và nhận phản hồi AI")
    public ApiResponse<SendMessageResponse> sendMessage(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String sessionId,
            @Valid @RequestBody SendMessageRequest request
    ) {
        return ApiResponse.ok(chatService.sendMessage(sessionId, principal.getId(), request));
    }

    @PatchMapping("/api/v1/chat/sessions/{sessionId}")
    @Operation(summary = "Đổi title chat session")
    public ApiResponse<ChatSessionResponse> updateSession(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String sessionId,
            @Valid @RequestBody UpdateChatSessionRequest request
    ) {
        return ApiResponse.ok(chatService.updateSession(sessionId, principal.getId(), request));
    }

    @DeleteMapping("/api/v1/chat/sessions/{sessionId}")
    @Operation(summary = "Xóa chat session")
    public ApiResponse<Void> deleteSession(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String sessionId
    ) {
        chatService.deleteSession(sessionId, principal.getId());
        return ApiResponse.ok("Chat session deleted", null);
    }
}
