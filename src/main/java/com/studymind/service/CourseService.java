package com.studymind.service;

import com.studymind.dto.course.CourseDetailResponse;
import com.studymind.dto.course.CourseResponse;
import com.studymind.dto.course.CreateCourseRequest;
import com.studymind.dto.course.UpdateCourseRequest;
import com.studymind.dto.document.DocumentResponse;
import com.studymind.exception.BadRequestException;
import com.studymind.model.Course;
import com.studymind.repository.CourseRepository;
import com.studymind.repository.StudyDocumentRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final StudyDocumentRepository documentRepository;
    private final CourseAccessService courseAccessService;
    private final DocumentService documentService;

    public CourseService(
            CourseRepository courseRepository,
            StudyDocumentRepository documentRepository,
            CourseAccessService courseAccessService,
            DocumentService documentService
    ) {
        this.courseRepository = courseRepository;
        this.documentRepository = documentRepository;
        this.courseAccessService = courseAccessService;
        this.documentService = documentService;
    }

    public CourseResponse create(String userId, CreateCourseRequest request) {
        Course course = new Course();
        course.setUserId(userId);
        course.setTitle(request.title().trim());
        course.setDescription(request.description() != null ? request.description().trim() : null);
        course.setDocumentCount(0);
        return CourseResponse.from(courseRepository.save(course));
    }

    public List<CourseResponse> listByUser(String userId) {
        return courseRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(CourseResponse::from)
                .toList();
    }

    public CourseDetailResponse getById(String courseId, String userId) {
        Course course = courseAccessService.requireOwnedCourse(courseId, userId);
        List<DocumentResponse> documents = documentRepository
                .findByCourseIdAndUserIdOrderByCreatedAtDesc(courseId, userId).stream()
                .map(DocumentResponse::from)
                .toList();
        return CourseDetailResponse.from(course, documents);
    }

    public CourseResponse update(String courseId, String userId, UpdateCourseRequest request) {
        Course course = courseAccessService.requireOwnedCourse(courseId, userId);
        if (request.title() != null) {
            if (request.title().isBlank()) {
                throw new BadRequestException("Course title cannot be blank");
            }
            course.setTitle(request.title().trim());
        }
        if (request.description() != null) {
            course.setDescription(request.description().trim());
        }
        return CourseResponse.from(courseRepository.save(course));
    }

    public void delete(String courseId, String userId) {
        courseAccessService.requireOwnedCourse(courseId, userId);
        List<String> documentIds = documentRepository
                .findByCourseIdAndUserIdOrderByCreatedAtDesc(courseId, userId).stream()
                .map(doc -> doc.getId())
                .toList();
        for (String documentId : documentIds) {
            documentService.delete(documentId, userId);
        }
        courseRepository.deleteById(courseId);
    }
}
