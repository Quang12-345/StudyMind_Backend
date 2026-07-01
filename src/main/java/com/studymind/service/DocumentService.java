package com.studymind.service;

import com.studymind.config.StorageProperties;
import com.studymind.dto.document.DocumentResponse;
import com.studymind.dto.document.UpdateDocumentRequest;
import com.studymind.exception.BadRequestException;
import com.studymind.model.StudyDocument;
import com.studymind.model.enums.DocumentStatus;
import com.studymind.repository.CourseRepository;
import com.studymind.repository.StudyDocumentRepository;
import com.studymind.service.storage.FileStorageService;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentService {

    private static final String PDF_CONTENT_TYPE = "application/pdf";

    private final StudyDocumentRepository documentRepository;
    private final DocumentAccessService documentAccessService;
    private final DocumentCleanupService documentCleanupService;
    private final FileStorageService fileStorageService;
    private final AiPipelineService aiPipelineService;
    private final CourseAccessService courseAccessService;
    private final com.studymind.repository.CourseRepository courseRepository;
    private final long maxFileSizeBytes;

    public DocumentService(
            StudyDocumentRepository documentRepository,
            DocumentAccessService documentAccessService,
            DocumentCleanupService documentCleanupService,
            FileStorageService fileStorageService,
            AiPipelineService aiPipelineService,
            CourseAccessService courseAccessService,
            CourseRepository courseRepository,
            StorageProperties storageProperties
    ) {
        this.documentRepository = documentRepository;
        this.documentAccessService = documentAccessService;
        this.documentCleanupService = documentCleanupService;
        this.fileStorageService = fileStorageService;
        this.aiPipelineService = aiPipelineService;
        this.courseAccessService = courseAccessService;
        this.courseRepository = courseRepository;
        this.maxFileSizeBytes = storageProperties.maxFileSizeBytes();
    }

    public DocumentResponse upload(String userId, String courseId, MultipartFile file, String title) {
        courseAccessService.requireOwnedCourse(courseId, userId);
        validatePdfFile(file);

        String originalFileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document.pdf";
        String storageKey = userId + "/" + courseId + "/" + UUID.randomUUID() + ".pdf";
        fileStorageService.store(file, storageKey);

        StudyDocument document = new StudyDocument();
        document.setUserId(userId);
        document.setCourseId(courseId);
        document.setTitle(title != null && !title.isBlank() ? title.trim() : stripExtension(originalFileName));
        document.setOriginalFileName(originalFileName);
        document.setStorageKey(storageKey);
        document.setFileSize(file.getSize());
        document.setMimeType(PDF_CONTENT_TYPE);
        document.setStatus(DocumentStatus.UPLOADED);
        document.setFileHash(computeHash(file));

        StudyDocument saved = documentRepository.save(document);
        refreshCourseDocumentCount(courseId);
        return DocumentResponse.from(saved);
    }

    public List<DocumentResponse> listByCourse(String courseId, String userId) {
        courseAccessService.requireOwnedCourse(courseId, userId);
        return documentRepository.findByCourseIdAndUserIdOrderByCreatedAtDesc(courseId, userId).stream()
                .map(DocumentResponse::from)
                .toList();
    }

    public List<DocumentResponse> listByUser(String userId) {
        return documentRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(DocumentResponse::from)
                .toList();
    }

    public DocumentResponse getById(String documentId, String userId) {
        StudyDocument document = documentAccessService.requireOwnedDocument(documentId, userId);
        return DocumentResponse.from(document);
    }

    public DocumentResponse updateTitle(String documentId, String userId, UpdateDocumentRequest request) {
        StudyDocument document = documentAccessService.requireOwnedDocument(documentId, userId);
        document.setTitle(request.title().trim());
        return DocumentResponse.from(documentRepository.save(document));
    }

    public void delete(String documentId, String userId) {
        StudyDocument document = documentAccessService.requireOwnedDocument(documentId, userId);
        String courseId = document.getCourseId();
        documentCleanupService.deleteRelatedData(documentId);
        fileStorageService.delete(document.getStorageKey());
        documentRepository.delete(document);
        if (courseId != null) {
            refreshCourseDocumentCount(courseId);
        }
    }

    public DocumentResponse process(String documentId, String userId) {
        StudyDocument document = documentAccessService.requireOwnedDocument(documentId, userId);
        if (document.getStatus() == DocumentStatus.UPLOADED || document.getStatus() == DocumentStatus.FAILED) {
            StudyDocument processed = aiPipelineService.processDocument(document);
            return DocumentResponse.from(processed);
        }
        if (document.getStatus() == DocumentStatus.READY) {
            return DocumentResponse.from(document);
        }
        throw new BadRequestException("Document is currently being processed");
    }

    public Resource loadFile(String documentId, String userId) {
        StudyDocument document = documentAccessService.requireOwnedDocument(documentId, userId);
        try {
            return new UrlResource(fileStorageService.load(document.getStorageKey()).toUri());
        } catch (IOException ex) {
            throw new BadRequestException("Unable to read stored file");
        }
    }

    public String getOriginalFileName(String documentId, String userId) {
        return documentAccessService.requireOwnedDocument(documentId, userId).getOriginalFileName();
    }

    private void validatePdfFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("PDF file is required");
        }
        if (file.getSize() > maxFileSizeBytes) {
            throw new BadRequestException("File exceeds maximum allowed size");
        }
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        boolean isPdf = PDF_CONTENT_TYPE.equals(contentType) || fileName.endsWith(".pdf");
        if (!isPdf) {
            throw new BadRequestException("Only PDF files are supported");
        }
    }

    private String stripExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
    }

    private String computeHash(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(file.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (IOException | NoSuchAlgorithmException ex) {
            return null;
        }
    }

    private void refreshCourseDocumentCount(String courseId) {
        courseRepository.findById(courseId).ifPresent(course -> {
            course.setDocumentCount((int) documentRepository.countByCourseId(courseId));
            courseRepository.save(course);
        });
    }
}
