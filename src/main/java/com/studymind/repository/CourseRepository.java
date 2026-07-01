package com.studymind.repository;

import com.studymind.model.Course;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<Course, String> {

    List<Course> findByUserIdOrderByUpdatedAtDesc(String userId);

    Optional<Course> findByIdAndUserId(String id, String userId);
}
