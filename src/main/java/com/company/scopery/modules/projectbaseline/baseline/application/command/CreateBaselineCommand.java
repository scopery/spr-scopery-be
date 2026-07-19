package com.company.scopery.modules.projectbaseline.baseline.application.command;
import java.util.UUID;
public record CreateBaselineCommand(
        UUID projectId, String name, String description,
        UUID sourceScheduleRunId, UUID sourceEstimationRunId,
        UUID sourceFinanceScenarioId, UUID sourceQuoteVersionId) {}
