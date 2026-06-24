package com.studymind.model;

import com.studymind.model.embedded.QuizAnswerRecord;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Document(collection = "quiz_attempts")
@CompoundIndex(name = "user_quiz_idx", def = "{'userId': 1, 'quizId': 1, 'completedAt': -1}")
public class QuizAttempt extends BaseDocument {

    @Indexed
    private String quizId;

    @Indexed
    private String userId;

    @Indexed
    private String documentId;

    private List<QuizAnswerRecord> answers = new ArrayList<>();
    private Integer score;
    private Integer totalQuestions;
    private Integer timeSpentSeconds;

    @Field("completed_at")
    private Instant completedAt;
}
