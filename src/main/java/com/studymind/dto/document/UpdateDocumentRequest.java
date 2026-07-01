package com.studymind.dto.document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateDocumentRequest(
        @NotBlank @Size(max = 200) String title
) {}
