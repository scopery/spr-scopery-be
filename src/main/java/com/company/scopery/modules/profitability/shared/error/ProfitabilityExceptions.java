package com.company.scopery.modules.profitability.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class ProfitabilityExceptions {
    private ProfitabilityExceptions() {}

    public static AppException profileNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.PROFILE_NOT_FOUND,
                "Profitability profile not found: " + id,
                Map.of("id", id == null ? "" : id.toString()));
    }

    public static AppException profileAlreadyExists(UUID projectId) {
        return new AppException(ProfitabilityErrorCatalog.PROFILE_ALREADY_EXISTS,
                "Profitability profile already exists for project: " + projectId,
                Map.of("projectId", projectId == null ? "" : projectId.toString()));
    }

    public static AppException summaryNotFound(UUID projectId) {
        return new AppException(ProfitabilityErrorCatalog.SUMMARY_NOT_FOUND,
                "Profitability summary not found for project: " + projectId,
                Map.of("projectId", projectId == null ? "" : projectId.toString()));
    }

    public static AppException planNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.PLAN_NOT_FOUND,
                "Profitability plan not found: " + id,
                Map.of("id", id == null ? "" : id.toString()));
    }

    public static AppException planVersionNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.PLAN_VERSION_NOT_FOUND,
                "Profitability plan version not found: " + id,
                Map.of("id", id == null ? "" : id.toString()));
    }

    public static AppException adjustmentNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.ADJUSTMENT_NOT_FOUND,
                "Profitability adjustment not found: " + id,
                Map.of("id", id == null ? "" : id.toString()));
    }

    public static AppException adjustmentAlreadyApplied(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.ADJUSTMENT_ALREADY_APPLIED,
                "Adjustment has already been applied: " + id,
                Map.of("id", id == null ? "" : id.toString()));
    }

    public static AppException riskFlagNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.RISK_FLAG_NOT_FOUND,
                "Profitability risk flag not found: " + id,
                Map.of("id", id == null ? "" : id.toString()));
    }

    public static AppException rateCardNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.RATE_CARD_NOT_FOUND,
                "Rate card not found: " + id,
                Map.of("id", id == null ? "" : id.toString()));
    }

    public static AppException rateCardCodeExists(String code) {
        return new AppException(ProfitabilityErrorCatalog.RATE_CARD_CODE_EXISTS,
                "Rate card code already exists: " + code,
                Map.of("code", code == null ? "" : code));
    }

    public static AppException costSourceNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.COST_SOURCE_NOT_FOUND,
                "Cost source not found: " + id,
                Map.of("id", id == null ? "" : id.toString()));
    }

    public static AppException revenueSourceNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.REVENUE_SOURCE_NOT_FOUND,
                "Revenue source not found: " + id,
                Map.of("id", id == null ? "" : id.toString()));
    }

    public static AppException costForecastNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.COST_FORECAST_NOT_FOUND,
                "Cost forecast not found: " + id,
                Map.of("id", id == null ? "" : id.toString()));
    }

    public static AppException revenueForecastNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.REVENUE_FORECAST_NOT_FOUND,
                "Revenue forecast not found: " + id,
                Map.of("id", id == null ? "" : id.toString()));
    }

    public static AppException varianceNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.VARIANCE_NOT_FOUND,
                "Profit variance not found: " + id,
                Map.of("id", id == null ? "" : id.toString()));
    }

    public static AppException snapshotNotFound(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.SNAPSHOT_NOT_FOUND,
                "Profit variance snapshot not found: " + id,
                Map.of("id", id == null ? "" : id.toString()));
    }

    public static AppException profileExists(UUID projectId) {
        return new AppException(ProfitabilityErrorCatalog.PROFILE_ALREADY_EXISTS,
                "Profitability profile already exists for project: " + projectId,
                Map.of("projectId", projectId == null ? "" : projectId.toString()));
    }

    public static AppException invalidCostSource(String reason) {
        return new AppException(ProfitabilityErrorCatalog.INVALID_COST_SOURCE,
                "Invalid cost source: " + reason,
                Map.of("reason", reason == null ? "" : reason));
    }

    public static AppException costSourceArchived(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.COST_SOURCE_ARCHIVED,
                "Cost source is archived: " + id,
                Map.of("id", id == null ? "" : id.toString()));
    }

    public static AppException invalidRevenueSource(String reason) {
        return new AppException(ProfitabilityErrorCatalog.INVALID_REVENUE_SOURCE,
                "Invalid revenue source: " + reason,
                Map.of("reason", reason == null ? "" : reason));
    }

    public static AppException revenueSourceArchived(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.REVENUE_SOURCE_ARCHIVED,
                "Revenue source is archived: " + id,
                Map.of("id", id == null ? "" : id.toString()));
    }

    public static AppException rateCardInvalid(String reason) {
        return new AppException(ProfitabilityErrorCatalog.RATE_CARD_INVALID,
                "Rate card operation invalid: " + reason,
                Map.of("reason", reason == null ? "" : reason));
    }

    public static AppException planInvalidStatus(UUID id, String reason) {
        return new AppException(ProfitabilityErrorCatalog.PLAN_INVALID_STATUS,
                "Plan has invalid status: " + reason,
                Map.of("id", id == null ? "" : id.toString(), "reason", reason == null ? "" : reason));
    }

    public static AppException planVersionImmutable(UUID id) {
        return new AppException(ProfitabilityErrorCatalog.PLAN_VERSION_IMMUTABLE,
                "Plan version is immutable: " + id,
                Map.of("id", id == null ? "" : id.toString()));
    }

    public static AppException portalSummaryNotVisible(UUID projectId) {
        return new AppException(ProfitabilityErrorCatalog.PORTAL_SUMMARY_NOT_VISIBLE,
                "Profitability summary is not visible on the portal for project: " + projectId,
                Map.of("projectId", projectId == null ? "" : projectId.toString()));
    }

    public static AppException riskFlagInvalidStatus(UUID id, Object status) {
        String statusStr = status == null ? "" : status.toString();
        return new AppException(ProfitabilityErrorCatalog.RISK_FLAG_INVALID_STATUS,
                "Risk flag " + id + " has invalid status for this operation: " + statusStr,
                Map.of("id", id == null ? "" : id.toString(), "status", statusStr));
    }

    public static AppException varianceCalculationFailed(String reason) {
        return new AppException(ProfitabilityErrorCatalog.VARIANCE_CALCULATION_FAILED,
                "Variance calculation failed: " + reason,
                Map.of("reason", reason == null ? "" : reason));
    }

    public static AppException accessDenied() {
        return new AppException(ProfitabilityErrorCatalog.ACCESS_DENIED,
                "Access denied to profitability resource",
                Map.of());
    }
}
