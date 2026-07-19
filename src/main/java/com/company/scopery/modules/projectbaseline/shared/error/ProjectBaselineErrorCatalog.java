package com.company.scopery.modules.projectbaseline.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum ProjectBaselineErrorCatalog implements ErrorCatalog {

    PROJECT_BASELINE_NOT_FOUND("PROJECT_BASELINE_NOT_FOUND", "Project baseline not found", HttpStatus.NOT_FOUND),
    PROJECT_BASELINE_PROJECT_ARCHIVED("PROJECT_BASELINE_PROJECT_ARCHIVED", "Cannot create baseline for an archived project", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_BASELINE_NUMBER_ALREADY_EXISTS("PROJECT_BASELINE_NUMBER_ALREADY_EXISTS", "Baseline number already exists in project", HttpStatus.CONFLICT),
    PROJECT_BASELINE_NOT_DRAFT("PROJECT_BASELINE_NOT_DRAFT", "Baseline must be DRAFT to edit", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_BASELINE_ALREADY_APPROVED("PROJECT_BASELINE_ALREADY_APPROVED", "Baseline is already approved", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_BASELINE_IMMUTABLE("PROJECT_BASELINE_IMMUTABLE", "Approved baseline is immutable", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_BASELINE_VALIDATION_FAILED("PROJECT_BASELINE_VALIDATION_FAILED", "Baseline validation failed", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_BASELINE_CURRENT_CONFLICT("PROJECT_BASELINE_CURRENT_CONFLICT", "Only one current baseline is allowed per project", HttpStatus.CONFLICT),
    PROJECT_BASELINE_SNAPSHOT_FAILED("PROJECT_BASELINE_SNAPSHOT_FAILED", "Baseline snapshot generation failed", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_BASELINE_SOURCE_MISMATCH("PROJECT_BASELINE_SOURCE_MISMATCH", "Baseline source artifact does not belong to project", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_BASELINE_SCHEDULE_NOT_COMPLETED("PROJECT_BASELINE_SCHEDULE_NOT_COMPLETED", "Schedule run must be COMPLETED", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_BASELINE_ESTIMATION_NOT_COMPLETED("PROJECT_BASELINE_ESTIMATION_NOT_COMPLETED", "Estimation run must be COMPLETED", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_BASELINE_FINANCE_NOT_APPROVED("PROJECT_BASELINE_FINANCE_NOT_APPROVED", "Finance scenario must be APPROVED or current", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_BASELINE_QUOTE_NOT_APPROVED("PROJECT_BASELINE_QUOTE_NOT_APPROVED", "Quote version must be APPROVED, SENT, or ACCEPTED", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_BASELINE_ACCESS_DENIED("PROJECT_BASELINE_ACCESS_DENIED", "Baseline access is denied", HttpStatus.FORBIDDEN),
    PROJECT_BASELINE_NOT_APPROVED("PROJECT_BASELINE_NOT_APPROVED", "Baseline must be APPROVED for this action", HttpStatus.UNPROCESSABLE_ENTITY),
    PROJECT_BASELINE_INVALID_STATUS("PROJECT_BASELINE_INVALID_STATUS", "Invalid baseline status for this action", HttpStatus.UNPROCESSABLE_ENTITY),

    CHANGE_REQUEST_NOT_FOUND("CHANGE_REQUEST_NOT_FOUND", "Change request not found", HttpStatus.NOT_FOUND),
    CHANGE_REQUEST_CODE_ALREADY_EXISTS("CHANGE_REQUEST_CODE_ALREADY_EXISTS", "Change request code already exists in project", HttpStatus.CONFLICT),
    CHANGE_REQUEST_PROJECT_ARCHIVED("CHANGE_REQUEST_PROJECT_ARCHIVED", "Cannot create change request for an archived project", HttpStatus.UNPROCESSABLE_ENTITY),
    CHANGE_REQUEST_NOT_DRAFT("CHANGE_REQUEST_NOT_DRAFT", "Change request must be DRAFT to edit", HttpStatus.UNPROCESSABLE_ENTITY),
    CHANGE_REQUEST_NOT_SUBMITTED("CHANGE_REQUEST_NOT_SUBMITTED", "Change request must be SUBMITTED", HttpStatus.UNPROCESSABLE_ENTITY),
    CHANGE_REQUEST_NOT_APPROVED("CHANGE_REQUEST_NOT_APPROVED", "Change request must be APPROVED", HttpStatus.UNPROCESSABLE_ENTITY),
    CHANGE_REQUEST_ALREADY_APPLIED("CHANGE_REQUEST_ALREADY_APPLIED", "Change request is already applied", HttpStatus.UNPROCESSABLE_ENTITY),
    CHANGE_REQUEST_ITEM_REQUIRED("CHANGE_REQUEST_ITEM_REQUIRED", "Change request must have at least one item", HttpStatus.UNPROCESSABLE_ENTITY),
    CHANGE_REQUEST_IMPACT_REQUIRED("CHANGE_REQUEST_IMPACT_REQUIRED", "Change impact is required before approval", HttpStatus.UNPROCESSABLE_ENTITY),
    CHANGE_REQUEST_REJECTION_REASON_REQUIRED("CHANGE_REQUEST_REJECTION_REASON_REQUIRED", "Rejection reason is required", HttpStatus.BAD_REQUEST),
    CHANGE_REQUEST_APPLY_FAILED("CHANGE_REQUEST_APPLY_FAILED", "Change request apply failed", HttpStatus.UNPROCESSABLE_ENTITY),
    CHANGE_REQUEST_PARTIAL_APPLY_NOT_SUPPORTED("CHANGE_REQUEST_PARTIAL_APPLY_NOT_SUPPORTED", "Partial apply is not supported", HttpStatus.UNPROCESSABLE_ENTITY),
    CHANGE_REQUEST_ACCESS_DENIED("CHANGE_REQUEST_ACCESS_DENIED", "Change request access is denied", HttpStatus.FORBIDDEN),
    CHANGE_REQUEST_INVALID_STATUS("CHANGE_REQUEST_INVALID_STATUS", "Invalid change request status for this action", HttpStatus.UNPROCESSABLE_ENTITY),

    CHANGE_REQUEST_ITEM_NOT_FOUND("CHANGE_REQUEST_ITEM_NOT_FOUND", "Change request item not found", HttpStatus.NOT_FOUND),
    CHANGE_REQUEST_ITEM_TARGET_MISMATCH("CHANGE_REQUEST_ITEM_TARGET_MISMATCH", "Change item target does not belong to project", HttpStatus.UNPROCESSABLE_ENTITY),
    CHANGE_REQUEST_ITEM_UNSUPPORTED_OPERATION("CHANGE_REQUEST_ITEM_UNSUPPORTED_OPERATION", "Unsupported change item operation", HttpStatus.UNPROCESSABLE_ENTITY),
    CHANGE_REQUEST_ITEM_INVALID_PAYLOAD("CHANGE_REQUEST_ITEM_INVALID_PAYLOAD", "Change item apply payload is invalid", HttpStatus.BAD_REQUEST),

    CHANGE_IMPACT_NOT_FOUND("CHANGE_IMPACT_NOT_FOUND", "Change impact not found", HttpStatus.NOT_FOUND),
    CHANGE_IMPACT_INVALID_CURRENCY("CHANGE_IMPACT_INVALID_CURRENCY", "Change impact currency is invalid", HttpStatus.BAD_REQUEST),
    CHANGE_IMPACT_INVALID_AMOUNT("CHANGE_IMPACT_INVALID_AMOUNT", "Change impact amount is invalid", HttpStatus.BAD_REQUEST),

    CHANGE_ORDER_NOT_FOUND("CHANGE_ORDER_NOT_FOUND", "Change order not found", HttpStatus.NOT_FOUND),
    CHANGE_ORDER_CHANGE_REQUEST_MISMATCH("CHANGE_ORDER_CHANGE_REQUEST_MISMATCH", "Change order does not belong to change request", HttpStatus.UNPROCESSABLE_ENTITY),
    CHANGE_ORDER_NOT_DRAFT("CHANGE_ORDER_NOT_DRAFT", "Change order must be DRAFT to edit", HttpStatus.UNPROCESSABLE_ENTITY),
    CHANGE_ORDER_NOT_APPROVABLE("CHANGE_ORDER_NOT_APPROVABLE", "Change order cannot be approved from current status", HttpStatus.UNPROCESSABLE_ENTITY),
    CHANGE_ORDER_CODE_ALREADY_EXISTS("CHANGE_ORDER_CODE_ALREADY_EXISTS", "Change order code already exists in project", HttpStatus.CONFLICT),
    CHANGE_ORDER_ACCESS_DENIED("CHANGE_ORDER_ACCESS_DENIED", "Change order access is denied", HttpStatus.FORBIDDEN),
    CHANGE_ORDER_INVALID_SOURCE("CHANGE_ORDER_INVALID_SOURCE", "Change order requires APPROVED or APPLIED change request", HttpStatus.UNPROCESSABLE_ENTITY),

    POST_BASELINE_EDIT_BLOCKED("POST_BASELINE_EDIT_BLOCKED", "Direct edit blocked after current baseline; create a ChangeRequest", HttpStatus.UNPROCESSABLE_ENTITY),
    BASELINE_OVERRIDE_PERMISSION_REQUIRED("BASELINE_OVERRIDE_PERMISSION_REQUIRED", "Baseline override permission required", HttpStatus.FORBIDDEN),
    BASELINE_OVERRIDE_REASON_REQUIRED("BASELINE_OVERRIDE_REASON_REQUIRED", "Baseline override reason is required", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ProjectBaselineErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
