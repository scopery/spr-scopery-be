package com.company.scopery.modules.quality.teststep.application.command;
import java.util.List; import java.util.UUID;
public record BatchCreateTestCaseStepsCommand(UUID projectId, UUID testCaseId,
        List<StepItem> items) {
    public record StepItem(String action, String expectedResult, UUID screenId, UUID componentId) {}
}
