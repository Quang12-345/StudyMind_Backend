package com.studymind.service;

import com.studymind.exception.ForbiddenException;
import com.studymind.exception.ResourceNotFoundException;
import com.studymind.model.StudyDocument;
import com.studymind.repository.StudyDocumentRepository;
import org.springframework.stereotype.Service;

@Service
public class DocumentAccessService {

    private final StudyDocumentRepository documentRepository;

    public DocumentAccessService(StudyDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public StudyDocument requireOwnedDocument(String documentId, String userId) {
        StudyDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        if (!document.getUserId().equals(userId)) {
            throw new ForbiddenException("You do not have access to this document");
        }
        return document;
    }
}
