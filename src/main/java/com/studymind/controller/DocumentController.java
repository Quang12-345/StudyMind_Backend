package com.studymind.controller;

import com.studymind.dto.ApiResponse;
import com.studymind.dto.document.DocumentResponse;
import com.studymind.dto.document.UpdateDocumentRequest;
import com.studymind.security.UserPrincipal;
import com.studymind.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/documents")
@Tag(name = "Documents", description = "Upload và quản lý PDF")
@SecurityRequirement(name = "Bearer Authentication")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Upload PDF vào khóa học")
    public ApiResponse<DocumentResponse> upload(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestPart("courseId") String courseId,
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "title", required = false) String title
    ) {
        return ApiResponse.ok("Upload successful",
                documentService.upload(principal.getId(), courseId, file, title));
    }

    @GetMapping
    @Operation(summary = "Danh sách document của user")
    public ApiResponse<List<DocumentResponse>> list(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(documentService.listByUser(principal.getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết document")
    public ApiResponse<DocumentResponse> get(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id
    ) {
        return ApiResponse.ok(documentService.getById(id, principal.getId()));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Cập nhật title document")
    public ApiResponse<DocumentResponse> updateTitle(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id,
            @Valid @RequestBody UpdateDocumentRequest request
    ) {
        return ApiResponse.ok(documentService.updateTitle(id, principal.getId(), request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa document và dữ liệu liên quan")
    public ApiResponse<Void> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id
    ) {
        documentService.delete(id, principal.getId());
        return ApiResponse.ok("Document deleted", null);
    }

    @PostMapping("/{id}/process")
    @Operation(summary = "Xử lý AI: extract text, summary, flashcards, quiz, indexing")
    public ApiResponse<DocumentResponse> process(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id
    ) {
        return ApiResponse.ok("Processing completed", documentService.process(id, principal.getId()));
    }

    @GetMapping("/{id}/file")
    @Operation(summary = "Tải file PDF gốc")
    public ResponseEntity<Resource> downloadFile(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id
    ) {
        Resource resource = documentService.loadFile(id, principal.getId());
        String fileName = documentService.getOriginalFileName(id, principal.getId());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }
}
