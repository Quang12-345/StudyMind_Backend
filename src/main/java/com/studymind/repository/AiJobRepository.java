package com.studymind.repository;

import com.studymind.model.AiJob;
import com.studymind.model.enums.AiJobStatus;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AiJobRepository extends MongoRepository<AiJob, String> {

    List<AiJob> findByDocumentIdOrderByCreatedAtAsc(String documentId);

    List<AiJob> findByDocumentIdAndStatus(String documentId, AiJobStatus status);

    void deleteByDocumentId(String documentId);
}
