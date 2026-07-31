package com.company.scopery.modules.quality.teststep.http.request;
import jakarta.validation.constraints.NotEmpty;
import java.util.List; import java.util.UUID;
public record BatchCreateTestCaseStepsRequest(@NotEmpty List<StepItem> items) {
    public record StepItem(String action, String expectedResult, UUID screenId, UUID componentId) {}
}
