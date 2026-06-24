package com.studymind.service;

import com.studymind.dto.auth.CreateAdminRequest;
import com.studymind.dto.auth.CreateLecturerRequest;
import com.studymind.dto.auth.UserResponse;
import com.studymind.exception.BadRequestException;
import com.studymind.exception.ResourceNotFoundException;
import com.studymind.model.User;
import com.studymind.model.enums.UserRole;
import com.studymind.repository.UserRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final UserService userService;

    public AdminService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public UserResponse createAdmin(CreateAdminRequest request) {
        User user = userService.createUser(
                request.email(),
                request.password(),
                request.fullName(),
                UserRole.ADMIN,
                null,
                null,
                null
        );
        return UserResponse.from(user);
    }

    public UserResponse createLecturer(CreateLecturerRequest request) {
        User user = userService.createUser(
                request.email(),
                request.password(),
                request.fullName(),
                UserRole.LECTURER,
                null,
                request.department(),
                request.lecturerCode()
        );
        return UserResponse.from(user);
    }

    public List<UserResponse> listUsers(UserRole role) {
        List<User> users = role == null
                ? userRepository.findAll()
                : userRepository.findByRole(role);
        return users.stream().map(UserResponse::from).toList();
    }

    public UserResponse updateUserStatus(String userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() == UserRole.ADMIN && !active) {
            long activeAdmins = userRepository.findByRole(UserRole.ADMIN).stream()
                    .filter(u -> Boolean.TRUE.equals(u.getActive()))
                    .count();
            if (activeAdmins <= 1) {
                throw new BadRequestException("Cannot disable the last active admin account");
            }
        }

        user.setActive(active);
        return UserResponse.from(userRepository.save(user));
    }

    public Map<String, Long> getUserStats() {
        Map<String, Long> stats = new HashMap<>();
        for (UserRole role : UserRole.values()) {
            stats.put(role.name(), (long) userRepository.findByRole(role).size());
        }
        stats.put("TOTAL", userRepository.count());
        return stats;
    }
}
