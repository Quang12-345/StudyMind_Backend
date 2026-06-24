package com.studymind.model.embedded;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizAnswerRecord {

    private String questionId;
    private Integer selectedIndex;
    private String shortAnswer;
    private Boolean isCorrect;
}
