package com.company.scopery.modules.quality.coverage.application.command;
import java.util.UUID;
public record CreateTestCaseCoverageCommand(UUID projectId, UUID testCaseId, String targetType, UUID targetId, String coverageType) {}
