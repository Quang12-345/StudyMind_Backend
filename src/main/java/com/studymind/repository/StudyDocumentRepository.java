package com.studymind.repository;

import com.studymind.model.StudyDocument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudyDocumentRepository extends MongoRepository<StudyDocument, String> {

    List<StudyDocument> findByUserIdOrderByCreatedAtDesc(String userId);

    List<StudyDocument> findByCourseIdAndUserIdOrderByCreatedAtDesc(String courseId, String userId);

    long countByCourseId(String courseId);

    Optional<StudyDocument> findByIdAndUserId(String id, String userId);
}
