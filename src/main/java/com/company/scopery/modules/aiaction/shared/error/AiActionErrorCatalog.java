package com.company.scopery.modules.aiaction.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum AiActionErrorCatalog implements ErrorCatalog {

    // ── Tool ─────────────────────────────────────────────────────────────────

    AI_TOOL_NOT_FOUND(
            "AI_TOOL_NOT_FOUND",
            "Action tool not found",
            HttpStatus.NOT_FOUND),

    AI_TOOL_INACTIVE(
            "AI_TOOL_INACTIVE",
            "Action tool is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    AI_TOOL_SCHEMA_INVALID(
            "AI_TOOL_SCHEMA_INVALID",
            "Tool input schema validation failed",
            HttpStatus.BAD_REQUEST),

    AI_TOOL_ADAPTER_NOT_FOUND(
            "AI_TOOL_ADAPTER_NOT_FOUND",
            "No live adapter registered for this tool",
            HttpStatus.NOT_FOUND),

    AI_TOOL_DIRECT_MUTATION_FORBIDDEN(
            "AI_TOOL_DIRECT_MUTATION_FORBIDDEN",
            "Direct LLM invocation of mutation tools is forbidden; use action planning",
            HttpStatus.FORBIDDEN),

    AI_TOOL_PERMISSION_DENIED(
            "AI_TOOL_PERMISSION_DENIED",
            "Insufficient permission to use this tool",
            HttpStatus.FORBIDDEN),

    AI_TOOL_POLICY_DENIED(
            "AI_TOOL_POLICY_DENIED",
            "Tool action policy denied this operation",
            HttpStatus.UNPROCESSABLE_ENTITY),

    // ── Request ──────────────────────────────────────────────────────────────

    AI_ACTION_REQUEST_NOT_FOUND(
            "AI_ACTION_REQUEST_NOT_FOUND",
            "Action request not found",
            HttpStatus.NOT_FOUND),

    AI_ACTION_DOMAIN_CAPABILITY_UNAVAILABLE(
            "AI_ACTION_DOMAIN_CAPABILITY_UNAVAILABLE",
            "The required domain capability is not available",
            HttpStatus.NOT_FOUND),

    AI_ACTION_REQUIRED_INPUT_MISSING(
            "AI_ACTION_REQUIRED_INPUT_MISSING",
            "Required action input fields are missing",
            HttpStatus.UNPROCESSABLE_ENTITY),

    AI_ACTION_LEGACY_PAYLOAD_UNSUPPORTED(
            "AI_ACTION_LEGACY_PAYLOAD_UNSUPPORTED",
            "Legacy payload type is not supported for action planning",
            HttpStatus.BAD_REQUEST),

    // ── Plan ─────────────────────────────────────────────────────────────────

    AI_ACTION_PLAN_NOT_FOUND(
            "AI_ACTION_PLAN_NOT_FOUND",
            "Action plan not found",
            HttpStatus.NOT_FOUND),

    AI_ACTION_PLAN_INVALID_STATUS(
            "AI_ACTION_PLAN_INVALID_STATUS",
            "Action plan is not in a valid status for this operation",
            HttpStatus.BAD_REQUEST),

    AI_ACTION_PLAN_VALIDATION_FAILED(
            "AI_ACTION_PLAN_VALIDATION_FAILED",
            "Action plan validation failed",
            HttpStatus.UNPROCESSABLE_ENTITY),

    AI_ACTION_PLAN_STALE(
            "AI_ACTION_PLAN_STALE",
            "The action plan is stale and must be regenerated",
            HttpStatus.UNPROCESSABLE_ENTITY),

    AI_ACTION_PLAN_EXPIRED(
            "AI_ACTION_PLAN_EXPIRED",
            "The action plan has expired",
            HttpStatus.UNPROCESSABLE_ENTITY),

    // ── Preview ──────────────────────────────────────────────────────────────

    AI_ACTION_PREVIEW_REQUIRED(
            "AI_ACTION_PREVIEW_REQUIRED",
            "A valid preview is required before confirmation",
            HttpStatus.UNPROCESSABLE_ENTITY),

    AI_ACTION_PREVIEW_EXPIRED(
            "AI_ACTION_PREVIEW_EXPIRED",
            "The action preview has expired; regenerate before confirming",
            HttpStatus.UNPROCESSABLE_ENTITY),

    // ── Confirmation ─────────────────────────────────────────────────────────

    AI_ACTION_CONFIRMATION_REQUIRED(
            "AI_ACTION_CONFIRMATION_REQUIRED",
            "Explicit confirmation is required before execution",
            HttpStatus.UNPROCESSABLE_ENTITY),

    AI_ACTION_CONFIRMATION_INVALID(
            "AI_ACTION_CONFIRMATION_INVALID",
            "The confirmation hash or plan version does not match",
            HttpStatus.UNPROCESSABLE_ENTITY),

    AI_ACTION_CONFIRMATION_EXPIRED(
            "AI_ACTION_CONFIRMATION_EXPIRED",
            "The confirmation has expired; re-confirm before executing",
            HttpStatus.UNPROCESSABLE_ENTITY),

    // ── Execution ─────────────────────────────────────────────────────────────

    AI_ACTION_EXECUTION_ALREADY_EXISTS(
            "AI_ACTION_EXECUTION_ALREADY_EXISTS",
            "An execution already exists for this plan",
            HttpStatus.CONFLICT),

    AI_ACTION_EXECUTION_NOT_FOUND(
            "AI_ACTION_EXECUTION_NOT_FOUND",
            "Action execution not found",
            HttpStatus.NOT_FOUND),

    AI_ACTION_EXECUTION_INVALID_STATUS(
            "AI_ACTION_EXECUTION_INVALID_STATUS",
            "Action execution is not in a valid status for this operation",
            HttpStatus.BAD_REQUEST),

    AI_ACTION_EXECUTION_PARTIAL(
            "AI_ACTION_EXECUTION_PARTIAL",
            "Action execution completed with partial success",
            HttpStatus.UNPROCESSABLE_ENTITY),

    AI_ACTION_STEP_FAILED(
            "AI_ACTION_STEP_FAILED",
            "One or more action steps failed during execution",
            HttpStatus.UNPROCESSABLE_ENTITY),

    // ── Idempotency / concurrency ─────────────────────────────────────────────

    AI_ACTION_IDEMPOTENCY_CONFLICT(
            "AI_ACTION_IDEMPOTENCY_CONFLICT",
            "Idempotency key conflict: same key used with different request content",
            HttpStatus.CONFLICT),

    AI_ACTION_TARGET_VERSION_CONFLICT(
            "AI_ACTION_TARGET_VERSION_CONFLICT",
            "Target entity was modified since the plan was built; re-plan required",
            HttpStatus.CONFLICT),

    AI_ACTION_BASELINE_GUARD_BLOCKED(
            "AI_ACTION_BASELINE_GUARD_BLOCKED",
            "Baseline lock prevents this action",
            HttpStatus.UNPROCESSABLE_ENTITY),

    AI_ACTION_BATCH_LIMIT_EXCEEDED(
            "AI_ACTION_BATCH_LIMIT_EXCEEDED",
            "Action plan exceeds the allowed step or target batch limit",
            HttpStatus.UNPROCESSABLE_ENTITY),

    AI_ACTION_CONCURRENT_LIMIT_EXCEEDED(
            "AI_ACTION_CONCURRENT_LIMIT_EXCEEDED",
            "Maximum concurrent active executions reached for this user or workspace",
            HttpStatus.UNPROCESSABLE_ENTITY),

    // ── Compensation ─────────────────────────────────────────────────────────

    AI_ACTION_COMPENSATION_UNSUPPORTED(
            "AI_ACTION_COMPENSATION_UNSUPPORTED",
            "Compensation is not supported for one or more executed steps",
            HttpStatus.UNPROCESSABLE_ENTITY),

    // ── Realtime ─────────────────────────────────────────────────────────────

    AI_ACTION_EVENT_REPLAY_GAP(
            "AI_ACTION_EVENT_REPLAY_GAP",
            "Requested event sequence is older than retained events; reload current state",
            HttpStatus.CONFLICT),

    // ── General ──────────────────────────────────────────────────────────────

    AI_ACTION_FORBIDDEN(
            "AI_ACTION_FORBIDDEN",
            "This action type is forbidden",
            HttpStatus.FORBIDDEN);

    // ─────────────────────────────────────────────────────────────────────────

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    AiActionErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override public String code()           { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
