package com.studymind.service;

import com.studymind.exception.ForbiddenException;
import com.studymind.exception.ResourceNotFoundException;
import com.studymind.model.Course;
import com.studymind.repository.CourseRepository;
import org.springframework.stereotype.Service;

@Service
public class CourseAccessService {

    private final CourseRepository courseRepository;

    public CourseAccessService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course requireOwnedCourse(String courseId, String userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (!course.getUserId().equals(userId)) {
            throw new ForbiddenException("You do not have access to this course");
        }
        return course;
    }
}
