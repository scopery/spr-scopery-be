package com.company.scopery.modules.quality.teststep.application.command;
import java.util.UUID;
public record CreateTestStepCommand(UUID projectId, UUID testCaseId, Integer stepOrder, String actionText, String expectedResult, String dataNotes) {}
