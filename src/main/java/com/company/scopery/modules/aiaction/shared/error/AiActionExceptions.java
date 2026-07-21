package com.company.scopery.modules.aiaction.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class AiActionExceptions {

    private AiActionExceptions() {}

    // ── Tool ─────────────────────────────────────────────────────────────────

    public static AppException toolNotFound(String toolCode, String toolVersion) {
        return new AppException(AiActionErrorCatalog.AI_TOOL_NOT_FOUND,
                "Action tool not found: " + toolCode + " v" + toolVersion,
                Map.of("toolCode", toolCode, "toolVersion", toolVersion));
    }

    public static AppException toolInactive(String toolCode) {
        return new AppException(AiActionErrorCatalog.AI_TOOL_INACTIVE,
                "Action tool is not active: " + toolCode,
                Map.of("toolCode", toolCode));
    }

    public static AppException toolSchemaInvalid(String schemaCode, String details) {
        return new AppException(AiActionErrorCatalog.AI_TOOL_SCHEMA_INVALID,
                "Tool input schema validation failed for schema: " + schemaCode,
                Map.of("schemaCode", schemaCode, "details", details));
    }

    public static AppException toolAdapterNotFound(String toolCode) {
        return new AppException(AiActionErrorCatalog.AI_TOOL_ADAPTER_NOT_FOUND,
                "No live adapter registered for tool: " + toolCode,
                Map.of("toolCode", toolCode));
    }

    public static AppException directMutationForbidden(String toolCode) {
        return new AppException(AiActionErrorCatalog.AI_TOOL_DIRECT_MUTATION_FORBIDDEN,
                "Direct invocation of mutation tool is forbidden: " + toolCode,
                Map.of("toolCode", toolCode));
    }

    public static AppException toolPermissionDenied(String toolCode) {
        return new AppException(AiActionErrorCatalog.AI_TOOL_PERMISSION_DENIED,
                "Insufficient permission to use tool: " + toolCode,
                Map.of("toolCode", toolCode));
    }

    public static AppException toolPolicyDenied(String toolCode, String reason) {
        return new AppException(AiActionErrorCatalog.AI_TOOL_POLICY_DENIED,
                "Tool policy denied: " + toolCode,
                Map.of("toolCode", toolCode, "reason", reason));
    }

    // ── Request ──────────────────────────────────────────────────────────────

    public static AppException requestNotFound(UUID requestId) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_REQUEST_NOT_FOUND,
                "Action request not found: " + requestId,
                Map.of("requestId", requestId));
    }

    public static AppException domainCapabilityUnavailable(String capability) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_DOMAIN_CAPABILITY_UNAVAILABLE,
                "Domain capability unavailable: " + capability,
                Map.of("capability", capability));
    }

    public static AppException requiredInputMissing(String fieldNames) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_REQUIRED_INPUT_MISSING,
                "Required action input fields are missing: " + fieldNames,
                Map.of("fields", fieldNames));
    }

    public static AppException legacyPayloadUnsupported(String payloadType) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_LEGACY_PAYLOAD_UNSUPPORTED,
                "Legacy payload type is not supported: " + payloadType,
                Map.of("payloadType", payloadType));
    }

    // ── Plan ─────────────────────────────────────────────────────────────────

    public static AppException planNotFound(UUID planId) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_PLAN_NOT_FOUND,
                "Action plan not found: " + planId,
                Map.of("planId", planId));
    }

    public static AppException planInvalidStatus(UUID planId, String currentStatus) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_PLAN_INVALID_STATUS,
                "Action plan is not in a valid status for this operation: " + currentStatus,
                Map.of("planId", planId, "currentStatus", currentStatus));
    }

    public static AppException planValidationFailed(UUID planId, String reason) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_PLAN_VALIDATION_FAILED,
                "Action plan validation failed",
                Map.of("planId", planId, "reason", reason));
    }

    public static AppException planStale(UUID planId) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_PLAN_STALE,
                "The action plan is stale and must be regenerated: " + planId,
                Map.of("planId", planId));
    }

    public static AppException planExpired(UUID planId) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_PLAN_EXPIRED,
                "The action plan has expired: " + planId,
                Map.of("planId", planId));
    }

    // ── Preview ──────────────────────────────────────────────────────────────

    public static AppException previewRequired(UUID planId) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_PREVIEW_REQUIRED,
                "A valid preview is required before confirmation: " + planId,
                Map.of("planId", planId));
    }

    public static AppException previewExpired(UUID planId) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_PREVIEW_EXPIRED,
                "The action preview has expired; regenerate before confirming: " + planId,
                Map.of("planId", planId));
    }

    // ── Confirmation ─────────────────────────────────────────────────────────

    public static AppException confirmationRequired(UUID planId) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_CONFIRMATION_REQUIRED,
                "Explicit confirmation is required before execution: " + planId,
                Map.of("planId", planId));
    }

    public static AppException confirmationInvalid() {
        return new AppException(AiActionErrorCatalog.AI_ACTION_CONFIRMATION_INVALID,
                "The confirmation hash or plan version does not match");
    }

    public static AppException confirmationExpired(UUID confirmationId) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_CONFIRMATION_EXPIRED,
                "The confirmation has expired; re-confirm before executing: " + confirmationId,
                Map.of("confirmationId", confirmationId));
    }

    // ── Execution ─────────────────────────────────────────────────────────────

    public static AppException executionAlreadyExists(UUID planId) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_EXECUTION_ALREADY_EXISTS,
                "An execution already exists for plan: " + planId,
                Map.of("planId", planId));
    }

    public static AppException executionNotFound(UUID executionId) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_EXECUTION_NOT_FOUND,
                "Action execution not found: " + executionId,
                Map.of("executionId", executionId));
    }

    public static AppException executionInvalidStatus(UUID executionId, String currentStatus) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_EXECUTION_INVALID_STATUS,
                "Action execution is not in a valid status for this operation: " + currentStatus,
                Map.of("executionId", executionId, "currentStatus", currentStatus));
    }

    public static AppException executionPartial(UUID executionId) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_EXECUTION_PARTIAL,
                "Action execution completed with partial success: " + executionId,
                Map.of("executionId", executionId));
    }

    public static AppException stepFailed(UUID stepId, String errorCode) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_STEP_FAILED,
                "Action step failed: " + stepId,
                Map.of("stepId", stepId, "errorCode", errorCode));
    }

    // ── Idempotency / concurrency ─────────────────────────────────────────────

    public static AppException idempotencyConflict(String idempotencyKey) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_IDEMPOTENCY_CONFLICT,
                "Idempotency key used with different request content: " + idempotencyKey,
                Map.of("idempotencyKey", idempotencyKey));
    }

    public static AppException targetVersionConflict(String entityType, String entityId) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_TARGET_VERSION_CONFLICT,
                "Target " + entityType + " was modified since the plan was built: " + entityId,
                Map.of("entityType", entityType, "entityId", entityId));
    }

    public static AppException baselineGuardBlocked(String entityType, String entityId) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_BASELINE_GUARD_BLOCKED,
                "Baseline lock prevents this action on " + entityType + ": " + entityId,
                Map.of("entityType", entityType, "entityId", entityId));
    }

    public static AppException batchLimitExceeded(int limit, int actual) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_BATCH_LIMIT_EXCEEDED,
                "Plan exceeds allowed batch limit of " + limit + "; actual: " + actual,
                Map.of("limit", limit, "actual", actual));
    }

    public static AppException concurrentLimitExceeded(int limit) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_CONCURRENT_LIMIT_EXCEEDED,
                "Maximum concurrent executions reached: " + limit,
                Map.of("limit", limit));
    }

    // ── Compensation ─────────────────────────────────────────────────────────

    public static AppException compensationUnsupported(String toolCode) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_COMPENSATION_UNSUPPORTED,
                "Compensation is not supported for tool: " + toolCode,
                Map.of("toolCode", toolCode));
    }

    // ── Realtime ─────────────────────────────────────────────────────────────

    public static AppException eventReplayGap(UUID executionId, long requestedSequence) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_EVENT_REPLAY_GAP,
                "Requested event sequence is older than retained events; reload current state",
                Map.of("executionId", executionId, "requestedSequence", requestedSequence));
    }

    // ── General ──────────────────────────────────────────────────────────────

    public static AppException forbidden(String reason) {
        return new AppException(AiActionErrorCatalog.AI_ACTION_FORBIDDEN,
                "This action type is forbidden: " + reason,
                Map.of("reason", reason));
    }
}
