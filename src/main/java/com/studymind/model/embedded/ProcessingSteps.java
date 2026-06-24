package com.studymind.model.embedded;

import com.studymind.model.enums.ProcessingStepStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessingSteps {

    private ProcessingStepStatus extractText = ProcessingStepStatus.PENDING;
    private ProcessingStepStatus summary = ProcessingStepStatus.PENDING;
    private ProcessingStepStatus flashcards = ProcessingStepStatus.PENDING;
    private ProcessingStepStatus quiz = ProcessingStepStatus.PENDING;
    private ProcessingStepStatus indexing = ProcessingStepStatus.PENDING;
}
