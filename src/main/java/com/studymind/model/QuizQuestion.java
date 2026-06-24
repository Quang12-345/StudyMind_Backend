package com.studymind.model;

import com.studymind.model.enums.QuestionDifficulty;
import com.studymind.model.enums.QuizQuestionType;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "quiz_questions")
@CompoundIndex(name = "quiz_order_idx", def = "{'quizId': 1, 'order': 1}")
public class QuizQuestion extends BaseDocument {

    @Indexed
    private String quizId;

    @Indexed
    private String documentId;

    private String question;
    private QuizQuestionType type = QuizQuestionType.MULTIPLE_CHOICE;
    private List<String> options = new ArrayList<>();
    private Integer correctIndex;
    private String correctAnswer;
    private String explanation;
    private QuestionDifficulty difficulty = QuestionDifficulty.MEDIUM;
    private Integer order;
    private Integer sourcePage;
    private String sourceChunkId;
}
