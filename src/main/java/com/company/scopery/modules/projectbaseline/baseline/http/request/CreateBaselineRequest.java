package com.company.scopery.modules.projectbaseline.baseline.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateBaselineRequest(
        @NotBlank String name,
        String description,
        UUID sourceScheduleRunId,
        UUID sourceEstimationRunId,
        UUID sourceFinanceScenarioId,
        UUID sourceQuoteVersionId) {}
