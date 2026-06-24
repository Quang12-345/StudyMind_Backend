package com.studymind.model;

import com.studymind.model.enums.AiJobStatus;
import com.studymind.model.enums.AiJobType;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Document(collection = "ai_jobs")
@CompoundIndex(name = "document_status_idx", def = "{'documentId': 1, 'status': 1}")
public class AiJob extends BaseDocument {

    @Indexed
    private String documentId;

    @Indexed
    private String userId;

    private AiJobType type;
    private AiJobStatus status = AiJobStatus.PENDING;
    private String errorMessage;

    /** ID entity được tạo sau job (summaryId, deckId, quizId, ...). */
    private String resultId;

    @Field("started_at")
    private Instant startedAt;

    @Field("completed_at")
    private Instant completedAt;
}
