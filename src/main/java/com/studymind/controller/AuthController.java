package com.studymind.controller;

import com.studymind.dto.ApiResponse;
import com.studymind.dto.auth.AuthResponse;
import com.studymind.dto.auth.LoginRequest;
import com.studymind.dto.auth.RegisterStudentRequest;
import com.studymind.dto.auth.UserResponse;
import com.studymind.security.UserPrincipal;
import com.studymind.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Đăng ký sinh viên, đăng nhập theo role")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/student")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Đăng ký tài khoản sinh viên (tự đăng ký)")
    public ApiResponse<AuthResponse> registerStudent(@Valid @RequestBody RegisterStudentRequest request) {
        return ApiResponse.ok("Student registration successful", authService.registerStudent(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập (STUDENT, LECTURER, ADMIN)")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Thông tin user hiện tại", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ApiResponse<UserResponse> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(authService.getCurrentUser(principal));
    }
}
