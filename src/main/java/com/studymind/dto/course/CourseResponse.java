package com.studymind.dto.course;

import com.studymind.model.Course;
import java.time.Instant;

public record CourseResponse(
        String id,
        String title,
        String description,
        Integer documentCount,
        Instant createdAt,
        Instant updatedAt
) {
    public static CourseResponse from(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getDocumentCount(),
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
}
