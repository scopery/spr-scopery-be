package com.company.scopery.modules.estimation.calculation;

/**
 * Options controlling estimation engine inclusion and billing preview.
 */
public record EstimationEngineOptions(
        boolean includeCompletedTasks,
        boolean includeCancelledTasks,
        boolean includeArchivedTasks,
        boolean useBillingPreview,
        boolean markAsCurrent
) {
    public static EstimationEngineOptions defaults() {
        return new EstimationEngineOptions(false, false, false, true, true);
    }
}
