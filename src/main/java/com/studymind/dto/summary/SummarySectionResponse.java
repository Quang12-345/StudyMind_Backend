package com.studymind.dto.summary;

import com.studymind.model.embedded.SummarySection;

public record SummarySectionResponse(String title, String content) {

    public static SummarySectionResponse from(SummarySection section) {
        return new SummarySectionResponse(section.getTitle(), section.getContent());
    }
}
