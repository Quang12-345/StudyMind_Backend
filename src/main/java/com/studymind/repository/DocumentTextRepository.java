package com.studymind.repository;

import com.studymind.model.DocumentText;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentTextRepository extends MongoRepository<DocumentText, String> {

    Optional<DocumentText> findByDocumentId(String documentId);

    void deleteByDocumentId(String documentId);
}
