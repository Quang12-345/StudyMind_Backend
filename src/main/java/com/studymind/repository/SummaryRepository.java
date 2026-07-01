package com.studymind.repository;

import com.studymind.model.Summary;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SummaryRepository extends MongoRepository<Summary, String> {

    Optional<Summary> findByDocumentIdAndIsLatestTrue(String documentId);

    void deleteByDocumentId(String documentId);
}
