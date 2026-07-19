package com.company.scopery.modules.quote.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum QuoteErrorCatalog implements ErrorCatalog {

    QUOTE_NOT_FOUND("QUOTE_NOT_FOUND", "Quote not found", HttpStatus.NOT_FOUND),
    QUOTE_CODE_ALREADY_EXISTS("QUOTE_CODE_ALREADY_EXISTS", "Quote code already exists in project", HttpStatus.CONFLICT),
    QUOTE_PROJECT_ARCHIVED("QUOTE_PROJECT_ARCHIVED", "Cannot create quote for an archived project", HttpStatus.UNPROCESSABLE_ENTITY),
    QUOTE_SOURCE_FINANCE_NOT_FOUND("QUOTE_SOURCE_FINANCE_NOT_FOUND", "Source finance scenario not found", HttpStatus.NOT_FOUND),
    QUOTE_SOURCE_FINANCE_PROJECT_MISMATCH("QUOTE_SOURCE_FINANCE_PROJECT_MISMATCH", "Finance scenario does not belong to project", HttpStatus.UNPROCESSABLE_ENTITY),
    QUOTE_SOURCE_FINANCE_NOT_APPROVED("QUOTE_SOURCE_FINANCE_NOT_APPROVED", "Finance scenario must be APPROVED or current", HttpStatus.UNPROCESSABLE_ENTITY),
    QUOTE_ACCESS_DENIED("QUOTE_ACCESS_DENIED", "Quote access is denied", HttpStatus.FORBIDDEN),

    QUOTE_VERSION_NOT_FOUND("QUOTE_VERSION_NOT_FOUND", "Quote version not found", HttpStatus.NOT_FOUND),
    QUOTE_VERSION_NOT_DRAFT("QUOTE_VERSION_NOT_DRAFT", "Quote version must be DRAFT to edit", HttpStatus.UNPROCESSABLE_ENTITY),
    QUOTE_VERSION_IMMUTABLE("QUOTE_VERSION_IMMUTABLE", "Quote version is immutable", HttpStatus.UNPROCESSABLE_ENTITY),
    QUOTE_VERSION_INVALID_STATUS("QUOTE_VERSION_INVALID_STATUS", "Invalid quote version status for this action", HttpStatus.UNPROCESSABLE_ENTITY),
    QUOTE_VERSION_NO_LINES("QUOTE_VERSION_NO_LINES", "Quote version must have at least one line", HttpStatus.UNPROCESSABLE_ENTITY),
    QUOTE_VERSION_SUMMARY_INVALID("QUOTE_VERSION_SUMMARY_INVALID", "Quote version summary is invalid", HttpStatus.UNPROCESSABLE_ENTITY),
    QUOTE_VERSION_CURRENT_CONFLICT("QUOTE_VERSION_CURRENT_CONFLICT", "Only one current quote version is allowed per quote", HttpStatus.CONFLICT),
    QUOTE_VERSION_FINANCE_SNAPSHOT_INVALID("QUOTE_VERSION_FINANCE_SNAPSHOT_INVALID", "Finance snapshot is invalid", HttpStatus.UNPROCESSABLE_ENTITY),

    QUOTE_LINE_NOT_FOUND("QUOTE_LINE_NOT_FOUND", "Quote line not found", HttpStatus.NOT_FOUND),
    QUOTE_LINE_INVALID_QUANTITY("QUOTE_LINE_INVALID_QUANTITY", "Quote line quantity must be > 0", HttpStatus.BAD_REQUEST),
    QUOTE_LINE_INVALID_UNIT_PRICE("QUOTE_LINE_INVALID_UNIT_PRICE", "Quote line unit price must be >= 0", HttpStatus.BAD_REQUEST),
    QUOTE_LINE_CURRENCY_MISMATCH("QUOTE_LINE_CURRENCY_MISMATCH", "Quote line currency must match version", HttpStatus.UNPROCESSABLE_ENTITY),
    QUOTE_LINE_SOURCE_PHASE_MISMATCH("QUOTE_LINE_SOURCE_PHASE_MISMATCH", "Quote line phase does not belong to project", HttpStatus.UNPROCESSABLE_ENTITY),

    QUOTE_TERM_NOT_FOUND("QUOTE_TERM_NOT_FOUND", "Quote term not found", HttpStatus.NOT_FOUND),
    QUOTE_TERM_CONTENT_REQUIRED("QUOTE_TERM_CONTENT_REQUIRED", "Quote term content is required", HttpStatus.BAD_REQUEST),

    QUOTE_SOLVER_INVALID_COST_BASE("QUOTE_SOLVER_INVALID_COST_BASE", "Cost base must be >= 0", HttpStatus.BAD_REQUEST),
    QUOTE_SOLVER_INVALID_TARGET_MARGIN("QUOTE_SOLVER_INVALID_TARGET_MARGIN", "Target margin percent must be < 100", HttpStatus.BAD_REQUEST),
    QUOTE_SOLVER_FAILED("QUOTE_SOLVER_FAILED", "Target margin solver failed", HttpStatus.UNPROCESSABLE_ENTITY),

    QUOTE_DISCOUNT_INVALID("QUOTE_DISCOUNT_INVALID", "Discount is invalid", HttpStatus.BAD_REQUEST),
    QUOTE_DISCOUNT_REASON_REQUIRED("QUOTE_DISCOUNT_REASON_REQUIRED", "Discount reason is required when discount > 0", HttpStatus.BAD_REQUEST),
    QUOTE_DISCOUNT_APPROVAL_REQUIRED("QUOTE_DISCOUNT_APPROVAL_REQUIRED", "Discount above threshold requires QUOTE_DISCOUNT_APPROVE", HttpStatus.FORBIDDEN),

    QUOTE_SUBMIT_VALIDATION_FAILED("QUOTE_SUBMIT_VALIDATION_FAILED", "Quote submit validation failed", HttpStatus.UNPROCESSABLE_ENTITY),
    QUOTE_APPROVAL_PERMISSION_DENIED("QUOTE_APPROVAL_PERMISSION_DENIED", "Quote approval permission denied", HttpStatus.FORBIDDEN),
    QUOTE_REJECTION_REASON_REQUIRED("QUOTE_REJECTION_REASON_REQUIRED", "Rejection reason is required", HttpStatus.BAD_REQUEST),
    QUOTE_SEND_NOT_APPROVED("QUOTE_SEND_NOT_APPROVED", "Quote must be APPROVED before send", HttpStatus.UNPROCESSABLE_ENTITY),
    QUOTE_ACCEPTED_NO_CONTRACT_CREATED("QUOTE_ACCEPTED_NO_CONTRACT_CREATED", "Accepted quote does not create a contract", HttpStatus.UNPROCESSABLE_ENTITY),

    QUOTE_CURRENCY_MISMATCH("QUOTE_CURRENCY_MISMATCH", "Quote currency mismatch", HttpStatus.UNPROCESSABLE_ENTITY),
    QUOTE_TAX_NOT_SUPPORTED("QUOTE_TAX_NOT_SUPPORTED", "Tax calculation is not supported in Phase 18", HttpStatus.UNPROCESSABLE_ENTITY),
    QUOTE_MARGIN_ACCESS_DENIED("QUOTE_MARGIN_ACCESS_DENIED", "Quote margin access is denied", HttpStatus.FORBIDDEN);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    QuoteErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
