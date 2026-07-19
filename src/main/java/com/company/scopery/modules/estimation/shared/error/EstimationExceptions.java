package com.company.scopery.modules.estimation.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class EstimationExceptions {
    private EstimationExceptions() {}

    public static AppException runNotFound(UUID id) {
        return new AppException(EstimationErrorCatalog.ESTIMATION_RUN_NOT_FOUND,
                "Estimation run not found: " + id, Map.of("id", id));
    }

    public static AppException projectArchived(UUID projectId) {
        return new AppException(EstimationErrorCatalog.ESTIMATION_RUN_PROJECT_ARCHIVED,
                "Cannot create estimation run for archived project: " + projectId,
                Map.of("projectId", projectId));
    }

    public static AppException invalidMode(String mode) {
        return new AppException(EstimationErrorCatalog.ESTIMATION_RUN_INVALID_MODE,
                "Invalid calculation mode: " + mode, Map.of("calculationMode", mode == null ? "" : mode));
    }

    public static AppException invalidRateDateStrategy(String strategy) {
        return new AppException(EstimationErrorCatalog.ESTIMATION_RUN_INVALID_RATE_DATE_STRATEGY,
                "Invalid rate target date strategy: " + strategy,
                Map.of("rateTargetDateStrategy", strategy == null ? "" : strategy));
    }

    public static AppException invalidCurrencyPolicy(String policy) {
        return new AppException(EstimationErrorCatalog.ESTIMATION_RUN_INVALID_CURRENCY_POLICY,
                "Invalid currency policy: " + policy,
                Map.of("currencyPolicy", policy == null ? "" : policy));
    }

    public static AppException alreadyCompleted(UUID id) {
        return new AppException(EstimationErrorCatalog.ESTIMATION_RUN_ALREADY_COMPLETED,
                "Estimation run already completed: " + id, Map.of("id", id));
    }

    public static AppException notCancellable(UUID id) {
        return new AppException(EstimationErrorCatalog.ESTIMATION_RUN_NOT_CANCELLABLE,
                "Estimation run cannot be cancelled: " + id, Map.of("id", id));
    }

    public static AppException runFailed(String message) {
        return new AppException(EstimationErrorCatalog.ESTIMATION_RUN_FAILED, message);
    }

    public static AppException mixedCurrencyNotAllowed() {
        return new AppException(EstimationErrorCatalog.ESTIMATION_MIXED_CURRENCY_NOT_ALLOWED);
    }

    public static AppException accessDenied() {
        return new AppException(EstimationErrorCatalog.ESTIMATION_ACCESS_DENIED);
    }

    public static AppException rateDetailAccessDenied() {
        return new AppException(EstimationErrorCatalog.ESTIMATION_RATE_DETAIL_ACCESS_DENIED);
    }

    public static AppException runNotCompleted(UUID id) {
        return new AppException(EstimationErrorCatalog.ESTIMATION_RUN_NOT_COMPLETED,
                "Estimation run must be completed: " + id, Map.of("id", id));
    }

    public static AppException wbsCycleDetected() {
        return new AppException(EstimationErrorCatalog.ESTIMATION_WBS_CYCLE_DETECTED);
    }

    public static AppException wbsRollupFailed(String message) {
        return new AppException(EstimationErrorCatalog.ESTIMATION_WBS_ROLLUP_FAILED, message);
    }

    public static AppException phaseRollupFailed(String message) {
        return new AppException(EstimationErrorCatalog.ESTIMATION_PHASE_ROLLUP_FAILED, message);
    }

    public static AppException projectSummaryFailed(String message) {
        return new AppException(EstimationErrorCatalog.ESTIMATION_PROJECT_SUMMARY_FAILED, message);
    }
}
