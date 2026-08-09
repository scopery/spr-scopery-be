package com.company.scopery.modules.profitability.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum ProfitabilityErrorCatalog implements ErrorCatalog {

    PROFILE_NOT_FOUND("PROFITABILITY_PROFILE_NOT_FOUND", "Profitability profile not found", HttpStatus.NOT_FOUND),
    PROFILE_ALREADY_EXISTS("PROFITABILITY_PROFILE_EXISTS", "Profitability profile already exists for this project", HttpStatus.CONFLICT),
    SUMMARY_NOT_FOUND("PROFITABILITY_SUMMARY_NOT_FOUND", "Profitability summary not found", HttpStatus.NOT_FOUND),
    PLAN_NOT_FOUND("PROFITABILITY_PLAN_NOT_FOUND", "Profitability plan not found", HttpStatus.NOT_FOUND),
    PLAN_VERSION_NOT_FOUND("PROFITABILITY_PLAN_VERSION_NOT_FOUND", "Profitability plan version not found", HttpStatus.NOT_FOUND),
    ADJUSTMENT_NOT_FOUND("PROFITABILITY_ADJUSTMENT_NOT_FOUND", "Profitability adjustment not found", HttpStatus.NOT_FOUND),
    ADJUSTMENT_ALREADY_APPLIED("PROFITABILITY_ADJUSTMENT_APPLIED", "Adjustment has already been applied", HttpStatus.UNPROCESSABLE_ENTITY),
    RISK_FLAG_NOT_FOUND("PROFITABILITY_RISK_FLAG_NOT_FOUND", "Profitability risk flag not found", HttpStatus.NOT_FOUND),
    RATE_CARD_NOT_FOUND("PROFITABILITY_RATE_CARD_NOT_FOUND", "Rate card not found", HttpStatus.NOT_FOUND),
    RATE_CARD_CODE_EXISTS("PROFITABILITY_RATE_CARD_CODE_EXISTS", "Rate card code already exists", HttpStatus.CONFLICT),
    COST_SOURCE_NOT_FOUND("PROFITABILITY_COST_SOURCE_NOT_FOUND", "Cost source not found", HttpStatus.NOT_FOUND),
    REVENUE_SOURCE_NOT_FOUND("PROFITABILITY_REVENUE_SOURCE_NOT_FOUND", "Revenue source not found", HttpStatus.NOT_FOUND),
    COST_FORECAST_NOT_FOUND("PROFITABILITY_COST_FORECAST_NOT_FOUND", "Cost forecast not found", HttpStatus.NOT_FOUND),
    REVENUE_FORECAST_NOT_FOUND("PROFITABILITY_REVENUE_FORECAST_NOT_FOUND", "Revenue forecast not found", HttpStatus.NOT_FOUND),
    THRESHOLD_POLICY_NOT_FOUND("PROFITABILITY_THRESHOLD_POLICY_NOT_FOUND", "Threshold policy not found", HttpStatus.NOT_FOUND),
    VARIANCE_NOT_FOUND("PROFITABILITY_VARIANCE_NOT_FOUND", "Profit variance not found", HttpStatus.NOT_FOUND),
    SNAPSHOT_NOT_FOUND("PROFITABILITY_SNAPSHOT_NOT_FOUND", "Profit variance snapshot not found", HttpStatus.NOT_FOUND),
    INVALID_COST_SOURCE("PROFITABILITY_INVALID_COST_SOURCE", "Invalid cost source", HttpStatus.UNPROCESSABLE_ENTITY),
    COST_SOURCE_ARCHIVED("PROFITABILITY_COST_SOURCE_ARCHIVED", "Cost source is archived", HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_REVENUE_SOURCE("PROFITABILITY_INVALID_REVENUE_SOURCE", "Invalid revenue source", HttpStatus.UNPROCESSABLE_ENTITY),
    REVENUE_SOURCE_ARCHIVED("PROFITABILITY_REVENUE_SOURCE_ARCHIVED", "Revenue source is archived", HttpStatus.UNPROCESSABLE_ENTITY),
    RATE_CARD_INVALID("PROFITABILITY_RATE_CARD_INVALID", "Rate card operation invalid", HttpStatus.UNPROCESSABLE_ENTITY),
    PLAN_INVALID_STATUS("PROFITABILITY_PLAN_INVALID_STATUS", "Profitability plan has invalid status for this operation", HttpStatus.UNPROCESSABLE_ENTITY),
    PLAN_VERSION_IMMUTABLE("PROFITABILITY_PLAN_VERSION_IMMUTABLE", "Profitability plan version is immutable", HttpStatus.UNPROCESSABLE_ENTITY),
    PORTAL_SUMMARY_NOT_VISIBLE("PROFITABILITY_PORTAL_SUMMARY_NOT_VISIBLE", "Profitability summary is not visible on the portal", HttpStatus.UNPROCESSABLE_ENTITY),
    RISK_FLAG_INVALID_STATUS("PROFITABILITY_RISK_FLAG_INVALID_STATUS", "Risk flag has invalid status for this operation", HttpStatus.UNPROCESSABLE_ENTITY),
    VARIANCE_CALCULATION_FAILED("PROFITABILITY_VARIANCE_CALCULATION_FAILED", "Variance calculation failed", HttpStatus.UNPROCESSABLE_ENTITY),
    ACCESS_DENIED("PROFITABILITY_ACCESS_DENIED", "Access denied to profitability resource", HttpStatus.FORBIDDEN);

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
