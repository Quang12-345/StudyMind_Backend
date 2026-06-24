package com.studymind.service;

import com.studymind.exception.BadRequestException;
import com.studymind.model.User;
import com.studymind.model.enums.UserRole;
import com.studymind.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(
            String email,
            String password,
            String fullName,
            UserRole role,
            String studentCode,
            String department,
            String lecturerCode
    ) {
        String normalizedEmail = normalizeEmail(email);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Email already registered");
        }

        validateRoleFields(role, studentCode, department, lecturerCode);

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setFullName(fullName.trim());
        user.setRole(role);
        user.setActive(true);

        switch (role) {
            case STUDENT -> user.setStudentCode(normalizeCode(studentCode));
            case LECTURER -> {
                user.setDepartment(department.trim());
                user.setLecturerCode(normalizeCode(lecturerCode));
            }
            case ADMIN -> {
                // Admin accounts have no extra profile fields.
            }
        }

        return userRepository.save(user);
    }

    public void validateRoleFields(UserRole role, String studentCode, String department, String lecturerCode) {
        switch (role) {
            case STUDENT -> {
                if (isBlank(studentCode)) {
                    throw new BadRequestException("Student code is required for STUDENT role");
                }
                if (userRepository.existsByStudentCode(normalizeCode(studentCode))) {
                    throw new BadRequestException("Student code already registered");
                }
            }
            case LECTURER -> {
                if (isBlank(department)) {
                    throw new BadRequestException("Department is required for LECTURER role");
                }
                if (isBlank(lecturerCode)) {
                    throw new BadRequestException("Lecturer code is required for LECTURER role");
                }
                if (userRepository.existsByLecturerCode(normalizeCode(lecturerCode))) {
                    throw new BadRequestException("Lecturer code already registered");
                }
            }
            case ADMIN -> {
                // No extra fields required.
            }
        }
    }

    public String normalizeEmail(String email) {
        return email.toLowerCase().trim();
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
