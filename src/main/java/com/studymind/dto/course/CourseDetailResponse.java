package com.studymind.dto.course;

import com.studymind.dto.document.DocumentResponse;
import com.studymind.model.Course;
import java.time.Instant;
import java.util.List;

public record CourseDetailResponse(
        String id,
        String title,
        String description,
        Integer documentCount,
        List<DocumentResponse> documents,
        Instant createdAt,
        Instant updatedAt
) {
    public static CourseDetailResponse from(Course course, List<DocumentResponse> documents) {
        return new CourseDetailResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getDocumentCount(),
                documents,
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
}
