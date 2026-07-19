package com.company.scopery.modules.quality.defect.application.command;
import java.util.UUID;
public record CreateDefectCommand(UUID projectId, String code, String title, String description, String category,
        String severity, String priority, String reproductionSteps, String expectedResult, String actualResult,
        UUID sourceTestCaseResultId) {}
