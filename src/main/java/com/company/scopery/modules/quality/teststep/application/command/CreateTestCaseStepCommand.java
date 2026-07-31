package com.company.scopery.modules.quality.teststep.application.command;
import java.util.UUID;
public record CreateTestCaseStepCommand(UUID projectId, UUID testCaseId, String action, String expectedResult,
        UUID screenId, UUID componentId) {}
