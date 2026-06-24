package com.studymind.controller;

import com.studymind.dto.ApiResponse;
import com.studymind.dto.auth.CreateAdminRequest;
import com.studymind.dto.auth.CreateLecturerRequest;
import com.studymind.dto.auth.UserResponse;
import com.studymind.model.enums.UserRole;
import com.studymind.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Quản trị user — chỉ ADMIN")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/admins")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo tài khoản ADMIN")
    public ApiResponse<UserResponse> createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        return ApiResponse.ok("Admin account created", adminService.createAdmin(request));
    }

    @PostMapping("/lecturers")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cấp tài khoản giảng viên (ADMIN)")
    public ApiResponse<UserResponse> createLecturer(@Valid @RequestBody CreateLecturerRequest request) {
        return ApiResponse.ok("Lecturer account created", adminService.createLecturer(request));
    }

    @GetMapping("/users")
    @Operation(summary = "Danh sách user")
    public ApiResponse<List<UserResponse>> listUsers(@RequestParam(required = false) UserRole role) {
        return ApiResponse.ok(adminService.listUsers(role));
    }

    @PatchMapping("/users/{userId}/status")
    @Operation(summary = "Kích hoạt / vô hiệu hóa tài khoản")
    public ApiResponse<UserResponse> updateUserStatus(
            @PathVariable String userId,
            @RequestParam boolean active
    ) {
        return ApiResponse.ok(adminService.updateUserStatus(userId, active));
    }

    @GetMapping("/stats")
    @Operation(summary = "Thống kê số user theo role")
    public ApiResponse<Map<String, Long>> getStats() {
        return ApiResponse.ok(adminService.getUserStats());
    }
}
