package com.company.scopery.modules.quality.testcase.application.command;

import java.util.List;
import java.util.UUID;

public record CreateTestCaseCommand(
        UUID projectId,
        UUID testSuiteId,
        UUID useCaseId,
        String code,
        String title,
        String description,
        String type,
        String priority,
        String preconditions,
        String expectedResult,
        UUID assigneeId,
        String automationStatus,
        List<NestedStep> steps
) {
    /** Shell-only convenience. */
    public CreateTestCaseCommand(
            UUID projectId,
            UUID testSuiteId,
            UUID useCaseId,
            String code,
            String title,
            String description,
            String type,
            String priority,
            String preconditions,
            String expectedResult,
            UUID assigneeId,
            String automationStatus
    ) {
        this(projectId, testSuiteId, useCaseId, code, title, description, type, priority,
                preconditions, expectedResult, assigneeId, automationStatus, null);
    }

    public record NestedStep(
            String action,
            String expectedResult,
            UUID screenId,
            UUID componentId
    ) {}
}
