package com.studymind.model;

import com.studymind.model.embedded.ProcessingSteps;
import com.studymind.model.enums.DocumentStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "documents")
@CompoundIndex(name = "user_created_idx", def = "{'userId': 1, 'createdAt': -1}")
public class StudyDocument extends BaseDocument {

    @Indexed
    private String userId;

    private String title;
    private String originalFileName;

    /** Key/path nội bộ trên storage (local, S3, ...). */
    private String storageKey;

    /** URL public hoặc signed URL để client tải/preview PDF. */
    private String fileUrl;

    private Long fileSize;
    private Integer pageCount;
    private String mimeType = "application/pdf";
    private String fileHash;
    private String language;

    @Indexed
    private DocumentStatus status = DocumentStatus.UPLOADED;

    private ProcessingSteps processingSteps = new ProcessingSteps();
    private String errorMessage;

    /** Text đã extract xong chưa (nội dung full nằm ở collection document_texts). */
    private Boolean textExtracted = false;

    private Integer characterCount;

    /** ID các output AI — set sau khi xử lý xong từng bước. */
    private String summaryId;
    private String deckId;
    private String quizId;
}
