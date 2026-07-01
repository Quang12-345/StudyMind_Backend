package com.studymind.controller;

import com.studymind.dto.ApiResponse;
import com.studymind.dto.course.CourseDetailResponse;
import com.studymind.dto.course.CourseResponse;
import com.studymind.dto.course.CreateCourseRequest;
import com.studymind.dto.course.UpdateCourseRequest;
import com.studymind.dto.document.DocumentResponse;
import com.studymind.security.UserPrincipal;
import com.studymind.service.CourseService;
import com.studymind.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/courses")
@Tag(name = "Courses", description = "Quản lý dự án / khóa học")
@SecurityRequirement(name = "Bearer Authentication")
public class CourseController {

    private final CourseService courseService;
    private final DocumentService documentService;

    public CourseController(CourseService courseService, DocumentService documentService) {
        this.courseService = courseService;
        this.documentService = documentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo dự án / khóa học mới")
    public ApiResponse<CourseResponse> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateCourseRequest request
    ) {
        return ApiResponse.ok("Course created", courseService.create(principal.getId(), request));
    }

    @GetMapping
    @Operation(summary = "Danh sách khóa học của user")
    public ApiResponse<List<CourseResponse>> list(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(courseService.listByUser(principal.getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết khóa học kèm danh sách tài liệu")
    public ApiResponse<CourseDetailResponse> get(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id
    ) {
        return ApiResponse.ok(courseService.getById(id, principal.getId()));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Cập nhật khóa học")
    public ApiResponse<CourseResponse> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id,
            @Valid @RequestBody UpdateCourseRequest request
    ) {
        return ApiResponse.ok(courseService.update(id, principal.getId(), request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa khóa học và toàn bộ tài liệu liên quan")
    public ApiResponse<Void> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id
    ) {
        courseService.delete(id, principal.getId());
        return ApiResponse.ok("Course deleted", null);
    }

    @GetMapping("/{id}/documents")
    @Operation(summary = "Danh sách tài liệu trong khóa học")
    public ApiResponse<List<DocumentResponse>> listDocuments(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id
    ) {
        return ApiResponse.ok(documentService.listByCourse(id, principal.getId()));
    }

    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tải tài liệu PDF vào khóa học")
    public ApiResponse<DocumentResponse> uploadDocument(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id,
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "title", required = false) String title
    ) {
        return ApiResponse.ok(
                "Upload successful",
                documentService.upload(principal.getId(), id, file, title)
        );
    }
}
