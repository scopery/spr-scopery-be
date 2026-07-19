package com.company.scopery.modules.profitability.shared.error;

import com.company.scopery.common.exception.AppException;
import java.util.Map;
import java.util.UUID;

public final class ProfitabilityExceptions {

    private ProfitabilityExceptions() {}

    // ── Profile ───────────────────────────────────────────────────────────────

    public static AppException profileNotFound(UUID projectId) {
        return new AppException(ProfitabilityErrorCatalog.PROFILE_NOT_FOUND,
                "Profitability profile not found for project: " + projectId, Map.of("projectId", projectId));
    }

    public static AppException profileExists(UUID projectId) {
        return new AppException(ProfitabilityErrorCatalog.PROFILE_EXISTS,
                "Profitability profile already exists for project: " + projectId, Map.of("projectId", projectId));
    }

    public static AppException profileDisabled(UUID projectId) {
        return new AppException(ProfitabilityErrorCatalog.PROFILE_DISABLED,
                "Profitability profile is disabled for project: " + projectId, Map.of("projectId", projectId));
    }

    public static AppException accessDenied() {
        return new AppException(ProfitabilityErrorCatalog.PROFITABILITY_ACCESS_DENIED);
    }

    public static AppException sensitiveAccessDenied() {
        return new AppException(ProfitabilityErrorCatalog.SENSITIVE_ACCESS_DENIED);
    }

    // ── Revenue source ────────────────────────────────────────────────────────

    public static AppException revenueSourceNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.REVENUE_SOURCE_NOT_FOUND,
                "Revenue source not found: " + id, Map.of("revenueSourceId", id));
    }

    public static AppException revenueSourceTargetNotFound(String targetType, UUID targetId) {
        return new AppException(ProfitabilityErrorCatalog.REVENUE_SOURCE_TARGET_NOT_FOUND,
                "Revenue source target not found: " + targetType + " " + targetId,
                Map.of("targetType", targetType, "targetId", targetId));
    }

    public static AppException revenueSourceTargetMismatch(String targetType, UUID targetId) {
        return new AppException(ProfitabilityErrorCatalog.REVENUE_SOURCE_TARGET_MISMATCH,
                "Revenue source target does not belong to this project: " + targetType + " " + targetId,
                Map.of("targetType", targetType, "targetId", targetId));
    }

    public static AppException revenueSourceArchived(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.REVENUE_SOURCE_ARCHIVED,
                "Revenue source is archived: " + id, Map.of("revenueSourceId", id));
    }

    public static AppException invalidRevenueSource(String message) {
        return new AppException(ProfitabilityErrorCatalog.INVALID_REVENUE_SOURCE, message);
    }

    public static AppException revenueSourceInvalidStatus(UUID id, String currentStatus) {
        return new AppException(ProfitabilityErrorCatalog.REVENUE_SOURCE_INVALID_STATUS,
                "Revenue source " + id + " cannot be acted on in status: " + currentStatus,
                Map.of("revenueSourceId", id, "currentStatus", currentStatus));
    }

    // ── Revenue forecast ──────────────────────────────────────────────────────

    public static AppException revenueForecastNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.REVENUE_FORECAST_NOT_FOUND,
                "Revenue forecast not found: " + id, Map.of("revenueForecastId", id));
    }

    public static AppException revenueForecastRebuildFailed(String reason) {
        return new AppException(ProfitabilityErrorCatalog.REVENUE_FORECAST_REBUILD_FAILED,
                "Revenue forecast rebuild failed: " + reason, Map.of("reason", reason));
    }

    // ── Cost source ───────────────────────────────────────────────────────────

    public static AppException costSourceNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.COST_SOURCE_NOT_FOUND,
                "Cost source not found: " + id, Map.of("costSourceId", id));
    }

    public static AppException costSourceTargetNotFound(String targetType, UUID targetId) {
        return new AppException(ProfitabilityErrorCatalog.COST_SOURCE_TARGET_NOT_FOUND,
                "Cost source target not found: " + targetType + " " + targetId,
                Map.of("targetType", targetType, "targetId", targetId));
    }

    public static AppException costSourceTargetMismatch(String targetType, UUID targetId) {
        return new AppException(ProfitabilityErrorCatalog.COST_SOURCE_TARGET_MISMATCH,
                "Cost source target does not belong to this project: " + targetType + " " + targetId,
                Map.of("targetType", targetType, "targetId", targetId));
    }

    public static AppException costSourceArchived(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.COST_SOURCE_ARCHIVED,
                "Cost source is archived: " + id, Map.of("costSourceId", id));
    }

    public static AppException invalidCostSource(String message) {
        return new AppException(ProfitabilityErrorCatalog.INVALID_COST_SOURCE, message);
    }

    public static AppException costSourceInvalidStatus(UUID id, String currentStatus) {
        return new AppException(ProfitabilityErrorCatalog.COST_SOURCE_INVALID_STATUS,
                "Cost source " + id + " cannot be acted on in status: " + currentStatus,
                Map.of("costSourceId", id, "currentStatus", currentStatus));
    }

    // ── Cost forecast ─────────────────────────────────────────────────────────

    public static AppException costForecastNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.COST_FORECAST_NOT_FOUND,
                "Cost forecast not found: " + id, Map.of("costForecastId", id));
    }

    public static AppException costForecastRebuildFailed(String reason) {
        return new AppException(ProfitabilityErrorCatalog.COST_FORECAST_REBUILD_FAILED,
                "Cost forecast rebuild failed: " + reason, Map.of("reason", reason));
    }

    // ── Rate card ─────────────────────────────────────────────────────────────

    public static AppException rateCardNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.RATE_CARD_NOT_FOUND,
                "Rate card not found: " + id, Map.of("rateCardId", id));
    }

    public static AppException rateCardInvalid(String reason) {
        return new AppException(ProfitabilityErrorCatalog.RATE_CARD_INVALID,
                "Invalid rate card: " + reason, Map.of("reason", reason));
    }

    public static AppException rateCardAccessDenied() {
        return new AppException(ProfitabilityErrorCatalog.RATE_CARD_ACCESS_DENIED);
    }

    public static AppException rateCardCodeExists(String code) {
        return new AppException(ProfitabilityErrorCatalog.RATE_CARD_CODE_EXISTS,
                "Rate card code already exists: " + code, Map.of("rateCode", code));
    }

    // ── Profitability plan ────────────────────────────────────────────────────

    public static AppException planNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.PROFITABILITY_PLAN_NOT_FOUND,
                "Profitability plan not found: " + id, Map.of("planId", id));
    }

    public static AppException planInvalidStatus(UUID id, String currentStatus) {
        return new AppException(ProfitabilityErrorCatalog.PROFITABILITY_PLAN_INVALID_STATUS,
                "Plan " + id + " cannot be acted on in status: " + currentStatus,
                Map.of("planId", id, "currentStatus", currentStatus));
    }

    // ── Plan version ──────────────────────────────────────────────────────────

    public static AppException planVersionNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.PROFITABILITY_PLAN_VERSION_NOT_FOUND,
                "Profitability plan version not found: " + id, Map.of("planVersionId", id));
    }

    public static AppException planVersionImmutable(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.PROFITABILITY_PLAN_VERSION_IMMUTABLE,
                "Finalized plan version is immutable: " + id, Map.of("planVersionId", id));
    }

    public static AppException planVersionFinalizeNotAllowed(UUID id, String reason) {
        return new AppException(ProfitabilityErrorCatalog.PROFITABILITY_PLAN_VERSION_FINALIZE_NOT_ALLOWED,
                "Plan version " + id + " cannot be finalized: " + reason,
                Map.of("planVersionId", id, "reason", reason));
    }

    public static AppException planVersionCurrentConflict(UUID planId) {
        return new AppException(ProfitabilityErrorCatalog.PROFITABILITY_PLAN_VERSION_CURRENT_CONFLICT,
                "Another version is already current for plan: " + planId, Map.of("planId", planId));
    }

    // ── Adjustment ────────────────────────────────────────────────────────────

    public static AppException adjustmentNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.ADJUSTMENT_NOT_FOUND,
                "Adjustment not found: " + id, Map.of("adjustmentId", id));
    }

    public static AppException adjustmentReasonRequired() {
        return new AppException(ProfitabilityErrorCatalog.ADJUSTMENT_REASON_REQUIRED);
    }

    public static AppException adjustmentInvalidStatus(UUID id, String currentStatus) {
        return new AppException(ProfitabilityErrorCatalog.ADJUSTMENT_INVALID_STATUS,
                "Adjustment " + id + " cannot be acted on in status: " + currentStatus,
                Map.of("adjustmentId", id, "currentStatus", currentStatus));
    }

    public static AppException adjustmentInvalidAmount(String reason) {
        return new AppException(ProfitabilityErrorCatalog.ADJUSTMENT_INVALID_AMOUNT,
                "Invalid adjustment amount: " + reason, Map.of("reason", reason));
    }

    // ── Snapshot / summary ────────────────────────────────────────────────────

    public static AppException snapshotNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.SNAPSHOT_NOT_FOUND,
                "Profitability snapshot not found: " + id, Map.of("snapshotId", id));
    }

    public static AppException snapshotCreateFailed(String reason) {
        return new AppException(ProfitabilityErrorCatalog.SNAPSHOT_CREATE_FAILED,
                "Profitability snapshot creation failed: " + reason, Map.of("reason", reason));
    }

    public static AppException summaryNotFound(UUID projectId) {
        return new AppException(ProfitabilityErrorCatalog.SUMMARY_NOT_FOUND,
                "Profitability summary not found for project: " + projectId, Map.of("projectId", projectId));
    }

    public static AppException summaryRebuildFailed(String reason) {
        return new AppException(ProfitabilityErrorCatalog.SUMMARY_REBUILD_FAILED,
                "Profitability summary rebuild failed: " + reason, Map.of("reason", reason));
    }

    // ── Risk flag ─────────────────────────────────────────────────────────────

    public static AppException riskFlagNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.RISK_FLAG_NOT_FOUND,
                "Profitability risk flag not found: " + id, Map.of("riskFlagId", id));
    }

    public static AppException riskFlagInvalidStatus(UUID id, String currentStatus) {
        return new AppException(ProfitabilityErrorCatalog.RISK_FLAG_INVALID_STATUS,
                "Risk flag " + id + " cannot be acted on in status: " + currentStatus,
                Map.of("riskFlagId", id, "currentStatus", currentStatus));
    }

    // ── Variance ──────────────────────────────────────────────────────────────

    public static AppException varianceCalculationFailed(String reason) {
        return new AppException(ProfitabilityErrorCatalog.VARIANCE_CALCULATION_FAILED,
                "Profitability variance calculation failed: " + reason, Map.of("reason", reason));
    }

    // ── Misc ──────────────────────────────────────────────────────────────────

    public static AppException thresholdPolicyInvalid(String reason) {
        return new AppException(ProfitabilityErrorCatalog.THRESHOLD_POLICY_INVALID,
                "Invalid threshold policy: " + reason, Map.of("reason", reason));
    }

    public static AppException currencyMismatch(String currency1, String currency2) {
        return new AppException(ProfitabilityErrorCatalog.CURRENCY_MISMATCH,
                "Cannot aggregate currencies without conversion policy: " + currency1 + " vs " + currency2,
                Map.of("currency1", currency1, "currency2", currency2));
    }

    public static AppException reportAccessDenied() {
        return new AppException(ProfitabilityErrorCatalog.REPORT_ACCESS_DENIED);
    }

    public static AppException portalSummaryNotVisible(UUID projectId) {
        return new AppException(ProfitabilityErrorCatalog.PORTAL_SUMMARY_NOT_VISIBLE,
                "Portal profitability summary is not visible for project: " + projectId, Map.of("projectId", projectId));
    }
}
