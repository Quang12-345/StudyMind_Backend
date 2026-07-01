package com.studymind.dto.ai;

import com.studymind.model.AiJob;
import com.studymind.model.enums.AiJobStatus;
import com.studymind.model.enums.AiJobType;
import java.time.Instant;

public record AiJobResponse(
        String id,
        String documentId,
        AiJobType type,
        AiJobStatus status,
        String resultId,
        String errorMessage,
        Instant startedAt,
        Instant completedAt,
        Instant createdAt
) {
    public static AiJobResponse from(AiJob job) {
        return new AiJobResponse(
                job.getId(),
                job.getDocumentId(),
                job.getType(),
                job.getStatus(),
                job.getResultId(),
                job.getErrorMessage(),
                job.getStartedAt(),
                job.getCompletedAt(),
                job.getCreatedAt()
        );
    }
}
