package com.company.scopery.modules.profitability.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum ProfitabilityErrorCatalog implements ErrorCatalog {

    // Profile
    PROFILE_NOT_FOUND("PROFITABILITY_PROFILE_NOT_FOUND", "Profitability profile not found", HttpStatus.NOT_FOUND),
    PROFILE_EXISTS("PROFITABILITY_PROFILE_ALREADY_EXISTS", "Profitability profile already exists for this project", HttpStatus.CONFLICT),
    PROFILE_DISABLED("PROFITABILITY_PROFILE_DISABLED", "Profitability profile is disabled", HttpStatus.UNPROCESSABLE_ENTITY),
    PROFITABILITY_ACCESS_DENIED("PROFITABILITY_PROFILE_ACCESS_DENIED", "Profitability access denied", HttpStatus.FORBIDDEN),
    SENSITIVE_ACCESS_DENIED("PROFITABILITY_SENSITIVE_ACCESS_DENIED", "Access denied to sensitive profitability data", HttpStatus.FORBIDDEN),

    // Revenue source
    REVENUE_SOURCE_NOT_FOUND("REVENUE_SOURCE_NOT_FOUND", "Revenue source not found", HttpStatus.NOT_FOUND),
    REVENUE_SOURCE_TARGET_NOT_FOUND("REVENUE_SOURCE_TARGET_NOT_FOUND", "Revenue source target not found", HttpStatus.NOT_FOUND),
    REVENUE_SOURCE_TARGET_MISMATCH("REVENUE_SOURCE_TARGET_MISMATCH", "Revenue source target does not belong to this project", HttpStatus.UNPROCESSABLE_ENTITY),
    REVENUE_SOURCE_ARCHIVED("REVENUE_SOURCE_ARCHIVED", "Revenue source is archived", HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_REVENUE_SOURCE("REVENUE_SOURCE_INVALID_AMOUNT", "Invalid revenue source amount or currency", HttpStatus.BAD_REQUEST),
    REVENUE_SOURCE_INVALID_STATUS("REVENUE_SOURCE_INVALID_STATUS", "Revenue source status does not allow this operation", HttpStatus.UNPROCESSABLE_ENTITY),

    // Revenue forecast
    REVENUE_FORECAST_NOT_FOUND("REVENUE_FORECAST_NOT_FOUND", "Revenue forecast not found", HttpStatus.NOT_FOUND),
    REVENUE_FORECAST_REBUILD_FAILED("REVENUE_FORECAST_REBUILD_FAILED", "Revenue forecast rebuild failed", HttpStatus.UNPROCESSABLE_ENTITY),

    // Cost source
    COST_SOURCE_NOT_FOUND("COST_SOURCE_NOT_FOUND", "Cost source not found", HttpStatus.NOT_FOUND),
    COST_SOURCE_TARGET_NOT_FOUND("COST_SOURCE_TARGET_NOT_FOUND", "Cost source target not found", HttpStatus.NOT_FOUND),
    COST_SOURCE_TARGET_MISMATCH("COST_SOURCE_TARGET_MISMATCH", "Cost source target does not belong to this project", HttpStatus.UNPROCESSABLE_ENTITY),
    COST_SOURCE_ARCHIVED("COST_SOURCE_ARCHIVED", "Cost source is archived", HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_COST_SOURCE("COST_SOURCE_INVALID_AMOUNT", "Invalid cost source amount or currency", HttpStatus.BAD_REQUEST),
    COST_SOURCE_INVALID_STATUS("COST_SOURCE_INVALID_STATUS", "Cost source status does not allow this operation", HttpStatus.UNPROCESSABLE_ENTITY),

    // Cost forecast
    COST_FORECAST_NOT_FOUND("COST_FORECAST_NOT_FOUND", "Cost forecast not found", HttpStatus.NOT_FOUND),
    COST_FORECAST_REBUILD_FAILED("COST_FORECAST_REBUILD_FAILED", "Cost forecast rebuild failed", HttpStatus.UNPROCESSABLE_ENTITY),

    // Rate card
    RATE_CARD_NOT_FOUND("RATE_CARD_NOT_FOUND", "Rate card not found", HttpStatus.NOT_FOUND),
    RATE_CARD_INVALID("RATE_CARD_INVALID", "Invalid rate card configuration", HttpStatus.BAD_REQUEST),
    RATE_CARD_ACCESS_DENIED("RATE_CARD_ACCESS_DENIED", "Access denied to rate card", HttpStatus.FORBIDDEN),
    RATE_CARD_CODE_EXISTS("RATE_CARD_CODE_ALREADY_EXISTS", "Rate card code already exists for this scope", HttpStatus.CONFLICT),

    // Profitability plan
    PROFITABILITY_PLAN_NOT_FOUND("PROFITABILITY_PLAN_NOT_FOUND", "Profitability plan not found", HttpStatus.NOT_FOUND),
    PROFITABILITY_PLAN_INVALID_STATUS("PROFITABILITY_PLAN_INVALID_STATUS", "Profitability plan status does not allow this operation", HttpStatus.UNPROCESSABLE_ENTITY),

    // Plan version
    PROFITABILITY_PLAN_VERSION_NOT_FOUND("PROFITABILITY_PLAN_VERSION_NOT_FOUND", "Profitability plan version not found", HttpStatus.NOT_FOUND),
    PROFITABILITY_PLAN_VERSION_IMMUTABLE("PROFITABILITY_PLAN_VERSION_IMMUTABLE", "Finalized plan version is immutable", HttpStatus.UNPROCESSABLE_ENTITY),
    PROFITABILITY_PLAN_VERSION_FINALIZE_NOT_ALLOWED("PROFITABILITY_PLAN_VERSION_FINALIZE_NOT_ALLOWED", "Plan version cannot be finalized in its current state", HttpStatus.UNPROCESSABLE_ENTITY),
    PROFITABILITY_PLAN_VERSION_CURRENT_CONFLICT("PROFITABILITY_PLAN_VERSION_CURRENT_CONFLICT", "Another plan version is already current", HttpStatus.CONFLICT),

    // Adjustment
    ADJUSTMENT_NOT_FOUND("PROFITABILITY_ADJUSTMENT_NOT_FOUND", "Profitability adjustment not found", HttpStatus.NOT_FOUND),
    ADJUSTMENT_REASON_REQUIRED("PROFITABILITY_ADJUSTMENT_REASON_REQUIRED", "Adjustment reason is required", HttpStatus.BAD_REQUEST),
    ADJUSTMENT_INVALID_STATUS("PROFITABILITY_ADJUSTMENT_INVALID_STATUS", "Adjustment status does not allow this operation", HttpStatus.UNPROCESSABLE_ENTITY),
    ADJUSTMENT_INVALID_AMOUNT("PROFITABILITY_ADJUSTMENT_INVALID_AMOUNT", "Adjustment amount must not be zero", HttpStatus.BAD_REQUEST),

    // Snapshot / summary
    SNAPSHOT_NOT_FOUND("PROFITABILITY_SNAPSHOT_NOT_FOUND", "Profitability snapshot not found", HttpStatus.NOT_FOUND),
    SNAPSHOT_CREATE_FAILED("PROFITABILITY_SNAPSHOT_CREATE_FAILED", "Profitability snapshot creation failed", HttpStatus.UNPROCESSABLE_ENTITY),
    SUMMARY_NOT_FOUND("PROFITABILITY_SUMMARY_NOT_FOUND", "Profitability summary not found", HttpStatus.NOT_FOUND),
    SUMMARY_REBUILD_FAILED("PROFITABILITY_SUMMARY_REBUILD_FAILED", "Profitability summary rebuild failed", HttpStatus.UNPROCESSABLE_ENTITY),

    // Risk flag
    RISK_FLAG_NOT_FOUND("PROFITABILITY_RISK_FLAG_NOT_FOUND", "Profitability risk flag not found", HttpStatus.NOT_FOUND),
    RISK_FLAG_INVALID_STATUS("PROFITABILITY_RISK_FLAG_INVALID_STATUS", "Risk flag status does not allow this operation", HttpStatus.UNPROCESSABLE_ENTITY),

    // Variance
    VARIANCE_CALCULATION_FAILED("PROFITABILITY_VARIANCE_CALCULATION_FAILED", "Profitability variance calculation failed", HttpStatus.UNPROCESSABLE_ENTITY),

    // Misc
    THRESHOLD_POLICY_INVALID("PROFITABILITY_THRESHOLD_POLICY_INVALID", "Invalid profitability threshold policy configuration", HttpStatus.BAD_REQUEST),
    CURRENCY_MISMATCH("PROFITABILITY_CURRENCY_MISMATCH", "Currency mismatch — cannot aggregate different currencies without conversion policy", HttpStatus.UNPROCESSABLE_ENTITY),
    REPORT_ACCESS_DENIED("PROFITABILITY_REPORT_ACCESS_DENIED", "Access denied to profitability report", HttpStatus.FORBIDDEN),
    PORTAL_SUMMARY_NOT_VISIBLE("PROFITABILITY_PORTAL_SUMMARY_NOT_VISIBLE", "Profitability summary is not visible on portal", HttpStatus.FORBIDDEN);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ProfitabilityErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
