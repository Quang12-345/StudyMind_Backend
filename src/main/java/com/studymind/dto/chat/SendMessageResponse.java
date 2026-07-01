package com.studymind.dto.chat;

public record SendMessageResponse(
        ChatMessageResponse userMessage,
        ChatMessageResponse assistantMessage
) {}
