package com.studymind.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "document_chunks")
@CompoundIndex(name = "document_chunk_idx", def = "{'documentId': 1, 'chunkIndex': 1}", unique = true)
public class DocumentChunk extends BaseDocument {

    @Indexed
    private String documentId;

    private Integer chunkIndex;
    private Integer pageNumber;
    private String content;
    private List<Double> embedding = new ArrayList<>();
    private Integer tokenCount;
}
