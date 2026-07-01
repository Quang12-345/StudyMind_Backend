package com.studymind.dto.chat;

import jakarta.validation.constraints.Size;

public record CreateChatSessionRequest(
        @Size(max = 200) String title
) {}
