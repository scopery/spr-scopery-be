package com.company.scopery.modules.quality.teststep.application.command;
import java.util.UUID;
public record UpdateTestCaseStepCommand(UUID projectId, UUID testCaseId, UUID stepId, String action,
        String expectedResult, UUID screenId, UUID componentId, Long version) {}
