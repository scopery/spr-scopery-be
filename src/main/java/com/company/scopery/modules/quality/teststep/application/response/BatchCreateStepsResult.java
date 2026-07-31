package com.company.scopery.modules.quality.teststep.application.response;
import java.util.List;
public record BatchCreateStepsResult(List<TestCaseStepResponse> created, List<BatchStepError> errors) {
    public record BatchStepError(int rowIndex, String reason) {}
}
