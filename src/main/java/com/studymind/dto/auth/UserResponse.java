package com.studymind.dto.auth;

import com.studymind.model.User;
import com.studymind.model.enums.UserRole;

public record UserResponse(
        String id,
        String email,
        String fullName,
        String avatarUrl,
        UserRole role,
        String studentCode,
        String department,
        String lecturerCode,
        Boolean active
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getAvatarUrl(),
                user.getRole(),
                user.getStudentCode(),
                user.getDepartment(),
                user.getLecturerCode(),
                user.getActive()
        );
    }
}
