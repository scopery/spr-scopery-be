package com.company.scopery.modules.quality.testcase.application.command;
import java.util.UUID;
public record UpdateTestCaseCommand(UUID projectId, UUID testCaseId, String title, String description,
        String code, String type, String priority, String status, UUID useCaseId, UUID assigneeId, String automationStatus,
        String preconditions, String expectedResult, Long version) {}
