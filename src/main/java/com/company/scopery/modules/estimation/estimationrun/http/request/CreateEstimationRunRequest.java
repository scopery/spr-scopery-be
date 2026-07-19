package com.company.scopery.modules.estimation.estimationrun.http.request;

import java.util.UUID;

public record CreateEstimationRunRequest(
        String name,
        String description,
        UUID scheduleRunId,
        String calculationMode,
        String rateTargetDateStrategy,
        String currencyPolicy,
        Options options
) {
    public record Options(
            Boolean includeCompletedTasks,
            Boolean includeCancelledTasks,
            Boolean includeArchivedTasks,
            Boolean useBillingPreview,
            Boolean markAsCurrent
    ) {}
}
