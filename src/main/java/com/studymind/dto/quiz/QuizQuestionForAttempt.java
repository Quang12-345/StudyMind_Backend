package com.studymind.dto.quiz;

import com.studymind.model.QuizQuestion;
import com.studymind.model.enums.QuestionDifficulty;
import com.studymind.model.enums.QuizQuestionType;
import java.util.List;

public record QuizQuestionForAttempt(
        String id,
        String question,
        QuizQuestionType type,
        List<String> options,
        Integer order,
        QuestionDifficulty difficulty
) {
    public static QuizQuestionForAttempt from(QuizQuestion question) {
        return new QuizQuestionForAttempt(
                question.getId(),
                question.getQuestion(),
                question.getType(),
                question.getOptions(),
                question.getOrder(),
                question.getDifficulty()
        );
    }
}
