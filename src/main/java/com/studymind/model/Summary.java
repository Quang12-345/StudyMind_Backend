package com.studymind.model;

import com.studymind.model.embedded.SummarySection;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "summaries")
@CompoundIndex(name = "document_version_idx", def = "{'documentId': 1, 'version': -1}")
public class Summary extends BaseDocument {

    @Indexed
    private String documentId;

    @Indexed
    private String userId;

    private Integer version = 1;
    private Boolean isLatest = true;

    private String shortSummary;
    private String detailedSummary;
    private List<String> keyPoints = new ArrayList<>();
    private List<SummarySection> sections = new ArrayList<>();
    private String model;
}
