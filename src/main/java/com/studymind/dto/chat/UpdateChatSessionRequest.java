package com.studymind.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateChatSessionRequest(
        @NotBlank @Size(max = 200) String title
) {}
