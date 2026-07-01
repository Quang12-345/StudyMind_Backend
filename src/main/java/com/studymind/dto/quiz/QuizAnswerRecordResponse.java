package com.studymind.dto.quiz;

import com.studymind.model.embedded.QuizAnswerRecord;

public record QuizAnswerRecordResponse(
        String questionId,
        Integer selectedIndex,
        String shortAnswer,
        Boolean isCorrect
) {
    public static QuizAnswerRecordResponse from(QuizAnswerRecord record) {
        return new QuizAnswerRecordResponse(
                record.getQuestionId(),
                record.getSelectedIndex(),
                record.getShortAnswer(),
                record.getIsCorrect()
        );
    }
}
