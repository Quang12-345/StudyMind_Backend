package com.studymind.controller;

import com.studymind.dto.ApiResponse;
import com.studymind.dto.quiz.QuizAttemptResponse;
import com.studymind.dto.quiz.QuizAttemptReviewResponse;
import com.studymind.dto.quiz.QuizDetailResponse;
import com.studymind.dto.quiz.SubmitQuizRequest;
import com.studymind.security.UserPrincipal;
import com.studymind.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Quiz", description = "Quiz ôn tập")
@SecurityRequirement(name = "Bearer Authentication")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/api/v1/documents/{documentId}/quiz")
    @Operation(summary = "Lấy quiz của document (ẩn đáp án)")
    public ApiResponse<QuizDetailResponse> getQuiz(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String documentId
    ) {
        return ApiResponse.ok(quizService.getQuizByDocumentId(documentId, principal.getId()));
    }

    @PostMapping("/api/v1/quizzes/{quizId}/attempts")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Nộp bài quiz")
    public ApiResponse<QuizAttemptResponse> submitAttempt(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String quizId,
            @Valid @RequestBody SubmitQuizRequest request
    ) {
        return ApiResponse.ok("Quiz submitted", quizService.submitAttempt(quizId, principal.getId(), request));
    }

    @GetMapping("/api/v1/quizzes/{quizId}/attempts")
    @Operation(summary = "Lịch sử làm bài")
    public ApiResponse<List<QuizAttemptResponse>> listAttempts(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String quizId
    ) {
        return ApiResponse.ok(quizService.listAttempts(quizId, principal.getId()));
    }

    @GetMapping("/api/v1/quiz-attempts/{attemptId}")
    @Operation(summary = "Xem chi tiết bài làm kèm đáp án")
    public ApiResponse<QuizAttemptReviewResponse> getAttemptReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String attemptId
    ) {
        return ApiResponse.ok(quizService.getAttemptReview(attemptId, principal.getId()));
    }
}
