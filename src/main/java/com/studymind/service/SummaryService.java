package com.studymind.service;

import com.studymind.dto.summary.SummaryResponse;
import com.studymind.exception.BadRequestException;
import com.studymind.exception.ResourceNotFoundException;
import com.studymind.model.StudyDocument;
import com.studymind.model.Summary;
import com.studymind.repository.SummaryRepository;
import org.springframework.stereotype.Service;

@Service
public class SummaryService {

    private final SummaryRepository summaryRepository;
    private final DocumentAccessService documentAccessService;
    private final AiPipelineService aiPipelineService;

    public SummaryService(
            SummaryRepository summaryRepository,
            DocumentAccessService documentAccessService,
            AiPipelineService aiPipelineService
    ) {
        this.summaryRepository = summaryRepository;
        this.documentAccessService = documentAccessService;
        this.aiPipelineService = aiPipelineService;
    }

    public SummaryResponse getByDocumentId(String documentId, String userId) {
        documentAccessService.requireOwnedDocument(documentId, userId);
        Summary summary = summaryRepository.findByDocumentIdAndIsLatestTrue(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Summary not found for this document"));
        return SummaryResponse.from(summary);
    }

    public SummaryResponse regenerate(String documentId, String userId) {
        StudyDocument document = documentAccessService.requireOwnedDocument(documentId, userId);
        Summary summary = aiPipelineService.regenerateSummary(document);
        return SummaryResponse.from(summary);
    }
}
