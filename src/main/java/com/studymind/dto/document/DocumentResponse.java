package com.studymind.dto.document;

import com.studymind.model.StudyDocument;
import com.studymind.model.embedded.ProcessingSteps;
import com.studymind.model.enums.DocumentStatus;
import java.time.Instant;

public record DocumentResponse(
        String id,
        String courseId,
        String title,
        String originalFileName,
        DocumentStatus status,
        ProcessingSteps processingSteps,
        Boolean textExtracted,
        Integer pageCount,
        Long fileSize,
        String summaryId,
        String deckId,
        String quizId,
        Instant createdAt,
        Instant updatedAt
) {
    public static DocumentResponse from(StudyDocument document) {
        return new DocumentResponse(
                document.getId(),
                document.getCourseId(),
                document.getTitle(),
                document.getOriginalFileName(),
                document.getStatus(),
                document.getProcessingSteps(),
                document.getTextExtracted(),
                document.getPageCount(),
                document.getFileSize(),
                document.getSummaryId(),
                document.getDeckId(),
                document.getQuizId(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }
}
