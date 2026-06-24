package com.studymind.model;

import com.studymind.model.enums.ChatRole;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "chat_messages")
@CompoundIndex(name = "session_created_idx", def = "{'sessionId': 1, 'createdAt': 1}")
public class ChatMessage extends BaseDocument {

    @Indexed
    private String sessionId;

    @Indexed
    private String documentId;

    @Indexed
    private String userId;

    private ChatRole role;
    private String content;
    private List<String> referencedChunkIds = new ArrayList<>();
    private Integer tokenCount;
}
