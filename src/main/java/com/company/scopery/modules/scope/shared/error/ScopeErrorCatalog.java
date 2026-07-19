package com.company.scopery.modules.scope.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum ScopeErrorCatalog implements ErrorCatalog {
    SCOPE_PACKAGE_NOT_FOUND("SCOPE_PACKAGE_NOT_FOUND", "Scope package not found", HttpStatus.NOT_FOUND),
    SCOPE_PACKAGE_CODE_EXISTS("SCOPE_PACKAGE_CODE_EXISTS", "Scope package code already exists", HttpStatus.CONFLICT),
    SCOPE_PACKAGE_IMMUTABLE("SCOPE_PACKAGE_IMMUTABLE", "Approved scope package is immutable", HttpStatus.UNPROCESSABLE_ENTITY),
    SCOPE_ITEM_NOT_FOUND("SCOPE_ITEM_NOT_FOUND", "Scope item not found", HttpStatus.NOT_FOUND),
    SCOPE_ITEM_TITLE_REQUIRED("SCOPE_ITEM_TITLE_REQUIRED", "Scope item title is required", HttpStatus.BAD_REQUEST),
    SCOPE_ITEM_INVALID_FLAGS("SCOPE_ITEM_INVALID_FLAGS", "Scope item cannot be both in-scope and out-of-scope", HttpStatus.BAD_REQUEST),
    DELIVERABLE_NOT_FOUND("DELIVERABLE_NOT_FOUND", "Deliverable not found", HttpStatus.NOT_FOUND),
    DELIVERABLE_INVALID_STATUS("DELIVERABLE_INVALID_STATUS", "Invalid deliverable status for this action", HttpStatus.UNPROCESSABLE_ENTITY),
    DELIVERABLE_CRITERIA_NOT_MET("DELIVERABLE_CRITERIA_NOT_MET", "Mandatory acceptance criteria are not satisfied", HttpStatus.UNPROCESSABLE_ENTITY),
    DELIVERABLE_REOPEN_REASON_REQUIRED("DELIVERABLE_REOPEN_REASON_REQUIRED", "Reopen reason is required", HttpStatus.BAD_REQUEST),
    ACCEPTANCE_CRITERIA_NOT_FOUND("ACCEPTANCE_CRITERIA_NOT_FOUND", "Acceptance criteria not found", HttpStatus.NOT_FOUND),
    ACCEPTANCE_CRITERIA_TITLE_REQUIRED("ACCEPTANCE_CRITERIA_TITLE_REQUIRED", "Acceptance criteria title is required", HttpStatus.BAD_REQUEST),
    SCOPE_ACCESS_DENIED("SCOPE_ACCESS_DENIED", "Scope access denied", HttpStatus.FORBIDDEN),
    SCOPE_PROJECT_ARCHIVED("SCOPE_PROJECT_ARCHIVED", "Cannot modify scope for archived project", HttpStatus.UNPROCESSABLE_ENTITY),
    EVIDENCE_NOT_FOUND("EVIDENCE_NOT_FOUND", "Acceptance evidence not found", HttpStatus.NOT_FOUND),
    REVIEW_NOT_FOUND("REVIEW_NOT_FOUND", "Deliverable review not found", HttpStatus.NOT_FOUND),
    REVIEW_INVALID_STATUS("REVIEW_INVALID_STATUS", "Invalid review status for this action", HttpStatus.UNPROCESSABLE_ENTITY),
    WBS_MAPPING_NOT_FOUND("WBS_MAPPING_NOT_FOUND", "Scope item WBS mapping not found", HttpStatus.NOT_FOUND),
    TASK_MAPPING_NOT_FOUND("TASK_MAPPING_NOT_FOUND", "Deliverable task mapping not found", HttpStatus.NOT_FOUND),
    WBS_NODE_NOT_FOUND("WBS_NODE_NOT_FOUND", "WBS node not found in project", HttpStatus.NOT_FOUND),
    TASK_NOT_FOUND("TASK_NOT_FOUND", "Task not found in project", HttpStatus.NOT_FOUND),
    QUOTE_VERSION_NOT_FOUND("QUOTE_VERSION_NOT_FOUND", "Quote version not found", HttpStatus.NOT_FOUND),
    QUOTE_VERSION_PROJECT_MISMATCH("QUOTE_VERSION_PROJECT_MISMATCH", "Quote version does not belong to project", HttpStatus.BAD_REQUEST),
    PACKAGE_NOT_APPROVED("PACKAGE_NOT_APPROVED", "Scope package must be approved for this action", HttpStatus.UNPROCESSABLE_ENTITY),
    DELIVERABLE_OPEN_REVIEW_EXISTS("DELIVERABLE_OPEN_REVIEW_EXISTS", "Deliverable already has an open review", HttpStatus.CONFLICT);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ScopeErrorCatalog(String c, String m, HttpStatus s) {
        code = c;
        defaultMessage = m;
        httpStatus = s;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultMessage() {
        return defaultMessage;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
