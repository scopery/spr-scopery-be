package com.company.scopery.modules.estimation.estimationrun.application.command;

import java.util.UUID;

public record CreateEstimationRunCommand(
        UUID projectId,
        String name,
        String description,
        UUID scheduleRunId,
        String calculationMode,
        String rateTargetDateStrategy,
        String currencyPolicy,
        boolean includeCompletedTasks,
        boolean includeCancelledTasks,
        boolean includeArchivedTasks,
        boolean useBillingPreview,
        boolean markAsCurrent
) {}
