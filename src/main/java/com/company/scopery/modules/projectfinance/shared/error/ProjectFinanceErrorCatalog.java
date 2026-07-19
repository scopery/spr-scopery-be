package com.company.scopery.modules.projectfinance.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum ProjectFinanceErrorCatalog implements ErrorCatalog {

    PROJECT_FINANCE_SCENARIO_NOT_FOUND(
            "PROJECT_FINANCE_SCENARIO_NOT_FOUND", "Finance scenario not found", HttpStatus.NOT_FOUND),
    PROJECT_FINANCE_SCENARIO_PROJECT_ARCHIVED(
            "PROJECT_FINANCE_SCENARIO_PROJECT_ARCHIVED", "Cannot create finance scenario for an archived project",
            HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_FINANCE_SCENARIO_NOT_DRAFT(
            "PROJECT_FINANCE_SCENARIO_NOT_DRAFT", "Finance scenario must be DRAFT to edit",
            HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_FINANCE_SCENARIO_ALREADY_APPROVED(
            "PROJECT_FINANCE_SCENARIO_ALREADY_APPROVED", "Finance scenario is already approved",
            HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_FINANCE_SCENARIO_INVALID_STATUS(
            "PROJECT_FINANCE_SCENARIO_INVALID_STATUS", "Invalid finance scenario status", HttpStatus.BAD_REQUEST),
    PROJECT_FINANCE_SCENARIO_CURRENT_CONFLICT(
            "PROJECT_FINANCE_SCENARIO_CURRENT_CONFLICT", "Only one current finance scenario is allowed per project",
            HttpStatus.CONFLICT),
    PROJECT_FINANCE_SCENARIO_ESTIMATION_NOT_FOUND(
            "PROJECT_FINANCE_SCENARIO_ESTIMATION_NOT_FOUND", "Estimation run not found for finance scenario",
            HttpStatus.NOT_FOUND),
    PROJECT_FINANCE_SCENARIO_ESTIMATION_NOT_COMPLETED(
            "PROJECT_FINANCE_SCENARIO_ESTIMATION_NOT_COMPLETED", "Estimation run must be COMPLETED",
            HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_FINANCE_SCENARIO_ESTIMATION_PROJECT_MISMATCH(
            "PROJECT_FINANCE_SCENARIO_ESTIMATION_PROJECT_MISMATCH", "Estimation run does not belong to project",
            HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_FINANCE_SCENARIO_CURRENCY_MISMATCH(
            "PROJECT_FINANCE_SCENARIO_CURRENCY_MISMATCH", "Currency does not match scenario or estimation",
            HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_FINANCE_SCENARIO_VALIDATION_FAILED(
            "PROJECT_FINANCE_SCENARIO_VALIDATION_FAILED", "Finance scenario validation failed",
            HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_FINANCE_SCENARIO_UNRESOLVED_RATES(
            "PROJECT_FINANCE_SCENARIO_UNRESOLVED_RATES", "Cannot approve scenario with unresolved estimation rates",
            HttpStatus.UNPROCESSABLE_ENTITY),

    PROJECT_PHASE_FINANCE_NOT_FOUND(
            "PROJECT_PHASE_FINANCE_NOT_FOUND", "Phase finance not found", HttpStatus.NOT_FOUND),
    PROJECT_PHASE_FINANCE_PHASE_MISMATCH(
            "PROJECT_PHASE_FINANCE_PHASE_MISMATCH", "Phase does not belong to project",
            HttpStatus.UNPROCESSABLE_ENTITY),

    PROJECT_CUSTOM_COST_NOT_FOUND(
            "PROJECT_CUSTOM_COST_NOT_FOUND", "Custom cost not found", HttpStatus.NOT_FOUND),
    PROJECT_CUSTOM_COST_INVALID_AMOUNT(
            "PROJECT_CUSTOM_COST_INVALID_AMOUNT", "Custom cost amount must be >= 0", HttpStatus.BAD_REQUEST),
    PROJECT_CUSTOM_COST_INVALID_CATEGORY(
            "PROJECT_CUSTOM_COST_INVALID_CATEGORY", "Invalid custom cost category", HttpStatus.BAD_REQUEST),
    PROJECT_CUSTOM_COST_PHASE_MISMATCH(
            "PROJECT_CUSTOM_COST_PHASE_MISMATCH", "Custom cost phase does not belong to project",
            HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_CUSTOM_COST_CURRENCY_MISMATCH(
            "PROJECT_CUSTOM_COST_CURRENCY_MISMATCH", "Custom cost currency must match scenario",
            HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_CUSTOM_COST_OTHER_REQUIRES_DESCRIPTION(
            "PROJECT_CUSTOM_COST_OTHER_REQUIRES_DESCRIPTION", "OTHER category requires description",
            HttpStatus.BAD_REQUEST),

    PROJECT_VENDOR_COST_NOT_FOUND(
            "PROJECT_VENDOR_COST_NOT_FOUND", "Vendor cost not found", HttpStatus.NOT_FOUND),
    PROJECT_VENDOR_COST_INVALID_AMOUNT(
            "PROJECT_VENDOR_COST_INVALID_AMOUNT", "Vendor cost amount must be >= 0", HttpStatus.BAD_REQUEST),
    PROJECT_VENDOR_COST_PHASE_MISMATCH(
            "PROJECT_VENDOR_COST_PHASE_MISMATCH", "Vendor cost phase does not belong to project",
            HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_VENDOR_COST_CURRENCY_MISMATCH(
            "PROJECT_VENDOR_COST_CURRENCY_MISMATCH", "Vendor cost currency must match scenario",
            HttpStatus.UNPROCESSABLE_ENTITY),

    PROJECT_REVENUE_SPLIT_INVALID_PERCENT_TOTAL(
            "PROJECT_REVENUE_SPLIT_INVALID_PERCENT_TOTAL", "Revenue percent split must sum to 100",
            HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_REVENUE_SPLIT_INVALID_AMOUNT_TOTAL(
            "PROJECT_REVENUE_SPLIT_INVALID_AMOUNT_TOTAL", "Revenue amount split must equal planned revenue",
            HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_REVENUE_SPLIT_DIRECT_COST_ZERO(
            "PROJECT_REVENUE_SPLIT_DIRECT_COST_ZERO", "Cannot use cost proportion with zero direct cost",
            HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_REVENUE_SPLIT_INVALID_METHOD(
            "PROJECT_REVENUE_SPLIT_INVALID_METHOD", "Invalid revenue split method", HttpStatus.BAD_REQUEST),

    PROJECT_OVERHEAD_POLICY_INVALID(
            "PROJECT_OVERHEAD_POLICY_INVALID", "Invalid overhead policy", HttpStatus.BAD_REQUEST),
    PROJECT_CONTINGENCY_INVALID(
            "PROJECT_CONTINGENCY_INVALID", "Invalid contingency policy", HttpStatus.BAD_REQUEST),

    PROJECT_FINANCE_ACCESS_DENIED(
            "PROJECT_FINANCE_ACCESS_DENIED", "Project finance access is denied", HttpStatus.FORBIDDEN),
    PROJECT_FINANCE_MARGIN_ACCESS_DENIED(
            "PROJECT_FINANCE_MARGIN_ACCESS_DENIED", "Project finance margin access is denied", HttpStatus.FORBIDDEN);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ProjectFinanceErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
