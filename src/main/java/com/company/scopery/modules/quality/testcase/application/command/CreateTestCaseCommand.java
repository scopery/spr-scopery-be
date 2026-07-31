package com.company.scopery.modules.quality.testcase.application.command;
import java.util.UUID;
public record CreateTestCaseCommand(UUID projectId, UUID testSuiteId, UUID useCaseId, String code, String title, String description,
        String type, String priority, String preconditions, String expectedResult,
        UUID assigneeId, String automationStatus) {}
