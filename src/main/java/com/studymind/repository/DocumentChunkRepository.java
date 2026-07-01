package com.studymind.repository;

import com.studymind.model.DocumentChunk;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentChunkRepository extends MongoRepository<DocumentChunk, String> {

    List<DocumentChunk> findByDocumentIdOrderByChunkIndexAsc(String documentId);

    void deleteByDocumentId(String documentId);
}
