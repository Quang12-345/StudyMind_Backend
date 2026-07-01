package com.studymind.dto.summary;

import com.studymind.model.Summary;
import java.time.Instant;
import java.util.List;

public record SummaryResponse(
        String id,
        String documentId,
        String shortSummary,
        String detailedSummary,
        List<String> keyPoints,
        List<SummarySectionResponse> sections,
        String model,
        Instant createdAt
) {
    public static SummaryResponse from(Summary summary) {
        return new SummaryResponse(
                summary.getId(),
                summary.getDocumentId(),
                summary.getShortSummary(),
                summary.getDetailedSummary(),
                summary.getKeyPoints(),
                summary.getSections().stream().map(SummarySectionResponse::from).toList(),
                summary.getModel(),
                summary.getCreatedAt()
        );
    }
}
