package com.company.scopery.modules.quality.testcase.application.command;
import java.util.List; import java.util.UUID;
public record BatchUpdateTestCasesCommand(UUID projectId, List<UUID> testCaseIds,
        String priority, String status, UUID assigneeId, String automationStatus) {}
