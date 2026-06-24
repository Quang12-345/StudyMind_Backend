package com.studymind.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "document_texts")
public class DocumentText extends BaseDocument {

    @Indexed(unique = true)
    private String documentId;

    private String content;
    private Integer characterCount;
    private Integer pageCount;
}
