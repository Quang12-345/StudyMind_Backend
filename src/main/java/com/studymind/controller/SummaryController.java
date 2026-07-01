package com.studymind.controller;

import com.studymind.dto.ApiResponse;
import com.studymind.dto.summary.SummaryResponse;
import com.studymind.security.UserPrincipal;
import com.studymind.service.SummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/documents/{documentId}/summary")
@Tag(name = "Summary", description = "AI tóm tắt nội dung")
@SecurityRequirement(name = "Bearer Authentication")
public class SummaryController {

    private final SummaryService summaryService;

    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @GetMapping
    @Operation(summary = "Lấy tóm tắt mới nhất của document")
    public ApiResponse<SummaryResponse> getSummary(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String documentId
    ) {
        return ApiResponse.ok(summaryService.getByDocumentId(documentId, principal.getId()));
    }

    @PostMapping("/regenerate")
    @Operation(summary = "Tạo lại tóm tắt")
    public ApiResponse<SummaryResponse> regenerate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String documentId
    ) {
        return ApiResponse.ok("Summary regenerated", summaryService.regenerate(documentId, principal.getId()));
    }
}
