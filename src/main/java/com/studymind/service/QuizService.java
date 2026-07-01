package com.studymind.service;

import com.studymind.dto.quiz.QuizAnswerRecordResponse;
import com.studymind.dto.quiz.QuizAnswerSubmission;
import com.studymind.dto.quiz.QuizAttemptResponse;
import com.studymind.dto.quiz.QuizAttemptReviewResponse;
import com.studymind.dto.quiz.QuizDetailResponse;
import com.studymind.dto.quiz.QuizQuestionReview;
import com.studymind.dto.quiz.SubmitQuizRequest;
import com.studymind.exception.BadRequestException;
import com.studymind.exception.ResourceNotFoundException;
import com.studymind.model.Quiz;
import com.studymind.model.QuizAttempt;
import com.studymind.model.QuizQuestion;
import com.studymind.model.embedded.QuizAnswerRecord;
import com.studymind.model.enums.QuizQuestionType;
import com.studymind.repository.QuizAttemptRepository;
import com.studymind.repository.QuizQuestionRepository;
import com.studymind.repository.QuizRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final DocumentAccessService documentAccessService;

    public QuizService(
            QuizRepository quizRepository,
            QuizQuestionRepository quizQuestionRepository,
            QuizAttemptRepository quizAttemptRepository,
            DocumentAccessService documentAccessService
    ) {
        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.documentAccessService = documentAccessService;
    }

    public QuizDetailResponse getQuizByDocumentId(String documentId, String userId) {
        documentAccessService.requireOwnedDocument(documentId, userId);
        Quiz quiz = quizRepository.findByDocumentIdAndIsLatestTrue(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found for this document"));
        List<QuizQuestion> questions = quizQuestionRepository.findByQuizIdOrderByOrderAsc(quiz.getId());
        return QuizDetailResponse.from(quiz, questions);
    }

    public QuizAttemptResponse submitAttempt(String quizId, String userId, SubmitQuizRequest request) {
        Quiz quiz = quizRepository.findByIdAndUserId(quizId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        List<QuizQuestion> questions = quizQuestionRepository.findByQuizIdOrderByOrderAsc(quizId);
        if (questions.isEmpty()) {
            throw new BadRequestException("Quiz has no questions");
        }

        Map<String, QuizQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(QuizQuestion::getId, Function.identity()));

        List<QuizAnswerRecord> records = new ArrayList<>();
        int score = 0;

        for (QuizAnswerSubmission submission : request.answers()) {
            QuizQuestion question = questionMap.get(submission.questionId());
            if (question == null) {
                throw new BadRequestException("Unknown question id: " + submission.questionId());
            }
            boolean correct = isCorrect(question, submission);
            if (correct) {
                score++;
            }

            QuizAnswerRecord record = new QuizAnswerRecord();
            record.setQuestionId(question.getId());
            record.setSelectedIndex(submission.selectedIndex());
            record.setShortAnswer(submission.shortAnswer());
            record.setIsCorrect(correct);
            records.add(record);
        }

        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuizId(quizId);
        attempt.setUserId(userId);
        attempt.setDocumentId(quiz.getDocumentId());
        attempt.setAnswers(records);
        attempt.setScore(score);
        attempt.setTotalQuestions(questions.size());
        attempt.setTimeSpentSeconds(request.timeSpentSeconds() != null ? request.timeSpentSeconds() : 0);
        attempt.setCompletedAt(Instant.now());

        return QuizAttemptResponse.from(quizAttemptRepository.save(attempt));
    }

    public List<QuizAttemptResponse> listAttempts(String quizId, String userId) {
        quizRepository.findByIdAndUserId(quizId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
        return quizAttemptRepository.findByQuizIdAndUserIdOrderByCompletedAtDesc(quizId, userId).stream()
                .map(QuizAttemptResponse::from)
                .toList();
    }

    public QuizAttemptReviewResponse getAttemptReview(String attemptId, String userId) {
        QuizAttempt attempt = quizAttemptRepository.findByIdAndUserId(attemptId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz attempt not found"));

        List<QuizQuestion> questions = quizQuestionRepository.findByQuizIdOrderByOrderAsc(attempt.getQuizId());
        Map<String, QuizAnswerRecord> answerMap = attempt.getAnswers().stream()
                .collect(Collectors.toMap(QuizAnswerRecord::getQuestionId, Function.identity()));

        List<QuizQuestionReview> reviews = questions.stream()
                .map(question -> {
                    QuizAnswerRecord answer = answerMap.get(question.getId());
                    return new QuizQuestionReview(
                            question.getId(),
                            question.getQuestion(),
                            question.getOptions(),
                            question.getCorrectIndex(),
                            question.getCorrectAnswer(),
                            question.getExplanation(),
                            answer != null ? answer.getSelectedIndex() : null,
                            answer != null ? answer.getShortAnswer() : null,
                            answer != null && Boolean.TRUE.equals(answer.getIsCorrect())
                    );
                })
                .toList();

        return new QuizAttemptReviewResponse(
                attempt.getId(),
                attempt.getQuizId(),
                attempt.getScore(),
                attempt.getTotalQuestions(),
                reviews,
                attempt.getCompletedAt()
        );
    }

    private boolean isCorrect(QuizQuestion question, QuizAnswerSubmission submission) {
        return switch (question.getType()) {
            case MULTIPLE_CHOICE, TRUE_FALSE -> submission.selectedIndex() != null
                    && submission.selectedIndex().equals(question.getCorrectIndex());
            case SHORT_ANSWER -> submission.shortAnswer() != null
                    && question.getCorrectAnswer() != null
                    && submission.shortAnswer().trim().equalsIgnoreCase(question.getCorrectAnswer().trim());
        };
    }
}
