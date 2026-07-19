package com.company.scopery.modules.estimation.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum EstimationErrorCatalog implements ErrorCatalog {

    ESTIMATION_RUN_NOT_FOUND(
            "ESTIMATION_RUN_NOT_FOUND", "Estimation run not found", HttpStatus.NOT_FOUND),
    ESTIMATION_RUN_PROJECT_ARCHIVED(
            "ESTIMATION_RUN_PROJECT_ARCHIVED", "Cannot create estimation run for an archived project", HttpStatus.UNPROCESSABLE_ENTITY),
    ESTIMATION_RUN_INVALID_MODE(
            "ESTIMATION_RUN_INVALID_MODE", "Invalid estimation calculation mode", HttpStatus.BAD_REQUEST),
    ESTIMATION_RUN_INVALID_RATE_DATE_STRATEGY(
            "ESTIMATION_RUN_INVALID_RATE_DATE_STRATEGY", "Invalid rate target date strategy", HttpStatus.BAD_REQUEST),
    ESTIMATION_RUN_INVALID_CURRENCY_POLICY(
            "ESTIMATION_RUN_INVALID_CURRENCY_POLICY", "Invalid currency policy", HttpStatus.BAD_REQUEST),
    ESTIMATION_RUN_ALREADY_COMPLETED(
            "ESTIMATION_RUN_ALREADY_COMPLETED", "Estimation run is already completed", HttpStatus.UNPROCESSABLE_ENTITY),
    ESTIMATION_RUN_NOT_CANCELLABLE(
            "ESTIMATION_RUN_NOT_CANCELLABLE", "Estimation run cannot be cancelled", HttpStatus.UNPROCESSABLE_ENTITY),
    ESTIMATION_RUN_FAILED(
            "ESTIMATION_RUN_FAILED", "Estimation run failed", HttpStatus.UNPROCESSABLE_ENTITY),
    ESTIMATION_TASK_NOT_FOUND(
            "ESTIMATION_TASK_NOT_FOUND", "Estimation task snapshot not found", HttpStatus.NOT_FOUND),
    ESTIMATION_TASK_INVALID_ESTIMATE(
            "ESTIMATION_TASK_INVALID_ESTIMATE", "Task estimate hours are invalid", HttpStatus.BAD_REQUEST),
    ESTIMATION_TASK_EXCLUDED(
            "ESTIMATION_TASK_EXCLUDED", "Task is excluded from estimation", HttpStatus.UNPROCESSABLE_ENTITY),
    ESTIMATION_TASK_COST_ROLE_NOT_RESOLVED(
            "ESTIMATION_TASK_COST_ROLE_NOT_RESOLVED", "Cost role could not be resolved for task", HttpStatus.UNPROCESSABLE_ENTITY),
    ESTIMATION_TASK_RATE_NOT_RESOLVED(
            "ESTIMATION_TASK_RATE_NOT_RESOLVED", "Rate could not be resolved for task", HttpStatus.UNPROCESSABLE_ENTITY),
    ESTIMATION_WBS_CYCLE_DETECTED(
            "ESTIMATION_WBS_CYCLE_DETECTED", "WBS cycle detected during roll-up", HttpStatus.UNPROCESSABLE_ENTITY),
    ESTIMATION_WBS_ROLLUP_FAILED(
            "ESTIMATION_WBS_ROLLUP_FAILED", "WBS estimate roll-up failed", HttpStatus.UNPROCESSABLE_ENTITY),
    ESTIMATION_PHASE_ROLLUP_FAILED(
            "ESTIMATION_PHASE_ROLLUP_FAILED", "Phase estimate roll-up failed", HttpStatus.UNPROCESSABLE_ENTITY),
    ESTIMATION_PROJECT_SUMMARY_FAILED(
            "ESTIMATION_PROJECT_SUMMARY_FAILED", "Project estimate summary failed", HttpStatus.UNPROCESSABLE_ENTITY),
    ESTIMATION_RATE_CARD_NOT_FOUND(
            "ESTIMATION_RATE_CARD_NOT_FOUND", "Rate card not found for estimation", HttpStatus.NOT_FOUND),
    ESTIMATION_RATE_VERSION_NOT_FOUND(
            "ESTIMATION_RATE_VERSION_NOT_FOUND", "Rate card version not found for estimation", HttpStatus.NOT_FOUND),
    ESTIMATION_RATE_LINE_NOT_FOUND(
            "ESTIMATION_RATE_LINE_NOT_FOUND", "Rate card line not found for estimation", HttpStatus.NOT_FOUND),
    ESTIMATION_RATE_UNRESOLVED(
            "ESTIMATION_RATE_UNRESOLVED", "Rate unresolved for estimation", HttpStatus.UNPROCESSABLE_ENTITY),
    ESTIMATION_MIXED_CURRENCY_NOT_ALLOWED(
            "ESTIMATION_MIXED_CURRENCY_NOT_ALLOWED", "Mixed currencies are not allowed for this estimation run", HttpStatus.UNPROCESSABLE_ENTITY),
    ESTIMATION_ACCESS_DENIED(
            "ESTIMATION_ACCESS_DENIED", "Estimation access is denied", HttpStatus.FORBIDDEN),
    ESTIMATION_RATE_DETAIL_ACCESS_DENIED(
            "ESTIMATION_RATE_DETAIL_ACCESS_DENIED", "Estimation rate detail access is denied", HttpStatus.FORBIDDEN),
    ESTIMATION_RUN_NOT_COMPLETED(
            "ESTIMATION_RUN_NOT_COMPLETED", "Estimation run must be completed to mark as current", HttpStatus.UNPROCESSABLE_ENTITY);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    EstimationErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
