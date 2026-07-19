package com.company.scopery.modules.aiplanning.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum AiPlanningErrorCatalog implements ErrorCatalog {
    AI_PLANNING_RUN_NOT_FOUND("AI_PLANNING_RUN_NOT_FOUND", "AI planning run not found", HttpStatus.NOT_FOUND),
    AI_PLANNING_RUN_NOT_CANCELLABLE("AI_PLANNING_RUN_NOT_CANCELLABLE", "AI planning run cannot be cancelled", HttpStatus.UNPROCESSABLE_ENTITY),
    AI_PLANNING_SUGGESTION_NOT_FOUND("AI_PLANNING_SUGGESTION_NOT_FOUND", "AI planning suggestion not found", HttpStatus.NOT_FOUND),
    AI_PLANNING_ITEM_NOT_FOUND("AI_PLANNING_ITEM_NOT_FOUND", "AI planning suggestion item not found", HttpStatus.NOT_FOUND),
    AI_PLANNING_INVALID_STATUS("AI_PLANNING_INVALID_STATUS", "Invalid AI planning status for this action", HttpStatus.UNPROCESSABLE_ENTITY),
    AI_PLANNING_ACCESS_DENIED("AI_PLANNING_ACCESS_DENIED", "AI planning access denied", HttpStatus.FORBIDDEN),
    AI_PLANNING_APPLY_NOT_ACCEPTED("AI_PLANNING_APPLY_NOT_ACCEPTED", "Only accepted suggestions/items can be applied", HttpStatus.UNPROCESSABLE_ENTITY),
    AI_PLANNING_APPLY_FAILED("AI_PLANNING_APPLY_FAILED", "AI planning apply failed", HttpStatus.UNPROCESSABLE_ENTITY),
    AI_PLANNING_BASELINE_REQUIRES_CHANGE_REQUEST("AI_PLANNING_BASELINE_REQUIRES_CHANGE_REQUEST", "Baselined project requires ChangeRequest for AI apply", HttpStatus.UNPROCESSABLE_ENTITY),
    AI_PLANNING_REJECTION_REASON_REQUIRED("AI_PLANNING_REJECTION_REASON_REQUIRED", "Rejection reason is required", HttpStatus.BAD_REQUEST),
    AI_PLANNING_INVALID_RUN_TYPE("AI_PLANNING_INVALID_RUN_TYPE", "Invalid AI planning run type", HttpStatus.BAD_REQUEST),
    AI_PLANNING_PROJECT_ARCHIVED("AI_PLANNING_PROJECT_ARCHIVED", "Cannot run AI planning on archived project", HttpStatus.UNPROCESSABLE_ENTITY),
    AI_PLANNING_CONTEXT_BUILD_FAILED("AI_PLANNING_CONTEXT_BUILD_FAILED", "Failed to build AI planning context", HttpStatus.UNPROCESSABLE_ENTITY),
    AI_PLANNING_FINANCE_PERMISSION_REQUIRED("AI_PLANNING_FINANCE_PERMISSION_REQUIRED", "Finance permission required for finance AI insight", HttpStatus.FORBIDDEN),
    AI_PLANNING_QUOTE_PERMISSION_REQUIRED("AI_PLANNING_QUOTE_PERMISSION_REQUIRED", "Quote permission required for quote AI draft", HttpStatus.FORBIDDEN);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    AiPlanningErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
