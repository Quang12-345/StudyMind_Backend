package com.studymind.repository;

import com.studymind.model.User;
import com.studymind.model.enums.UserRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByStudentCode(String studentCode);

    boolean existsByLecturerCode(String lecturerCode);

    boolean existsByRole(UserRole role);

    List<User> findByRole(UserRole role);
}
