package com.company.scopery.modules.projectbaseline.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class ProjectBaselineExceptions {
    private ProjectBaselineExceptions() {}

    public static AppException baselineNotFound(UUID id) {
        return new AppException(ProjectBaselineErrorCatalog.PROJECT_BASELINE_NOT_FOUND,
                "Project baseline not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException projectArchived(UUID projectId) {
        return new AppException(ProjectBaselineErrorCatalog.PROJECT_BASELINE_PROJECT_ARCHIVED,
                "Cannot create baseline for archived project: " + projectId, Map.of("projectId", projectId));
    }

    public static AppException baselineNotDraft(UUID id) {
        return new AppException(ProjectBaselineErrorCatalog.PROJECT_BASELINE_NOT_DRAFT,
                "Baseline must be DRAFT: " + id, Map.of("id", id));
    }

    public static AppException baselineImmutable(UUID id) {
        return new AppException(ProjectBaselineErrorCatalog.PROJECT_BASELINE_IMMUTABLE,
                "Approved baseline is immutable: " + id, Map.of("id", id));
    }

    public static AppException validationFailed(String message) {
        return new AppException(ProjectBaselineErrorCatalog.PROJECT_BASELINE_VALIDATION_FAILED, message);
    }

    public static AppException sourceMismatch(String field, UUID sourceId, UUID projectId) {
        return new AppException(ProjectBaselineErrorCatalog.PROJECT_BASELINE_SOURCE_MISMATCH,
                field + " does not belong to project",
                Map.of("field", field, "sourceId", sourceId == null ? "" : sourceId, "projectId", projectId));
    }

    public static AppException scheduleNotCompleted(UUID id) {
        return new AppException(ProjectBaselineErrorCatalog.PROJECT_BASELINE_SCHEDULE_NOT_COMPLETED,
                "Schedule run must be COMPLETED: " + id, Map.of("scheduleRunId", id));
    }

    public static AppException estimationNotCompleted(UUID id) {
        return new AppException(ProjectBaselineErrorCatalog.PROJECT_BASELINE_ESTIMATION_NOT_COMPLETED,
                "Estimation run must be COMPLETED: " + id, Map.of("estimationRunId", id));
    }

    public static AppException financeNotApproved(UUID id) {
        return new AppException(ProjectBaselineErrorCatalog.PROJECT_BASELINE_FINANCE_NOT_APPROVED,
                "Finance scenario must be APPROVED or current: " + id, Map.of("financeScenarioId", id));
    }

    public static AppException quoteNotApproved(UUID id) {
        return new AppException(ProjectBaselineErrorCatalog.PROJECT_BASELINE_QUOTE_NOT_APPROVED,
                "Quote version must be APPROVED/SENT/ACCEPTED: " + id, Map.of("quoteVersionId", id));
    }

    public static AppException baselineAccessDenied() {
        return new AppException(ProjectBaselineErrorCatalog.PROJECT_BASELINE_ACCESS_DENIED);
    }

    public static AppException baselineNotApproved(UUID id) {
        return new AppException(ProjectBaselineErrorCatalog.PROJECT_BASELINE_NOT_APPROVED,
                "Baseline must be APPROVED: " + id, Map.of("id", id));
    }

    public static AppException baselineInvalidStatus(UUID id, String action) {
        return new AppException(ProjectBaselineErrorCatalog.PROJECT_BASELINE_INVALID_STATUS,
                "Invalid baseline status for " + action + ": " + id,
                Map.of("id", id, "action", action == null ? "" : action));
    }

    public static AppException snapshotFailed(String message) {
        return new AppException(ProjectBaselineErrorCatalog.PROJECT_BASELINE_SNAPSHOT_FAILED, message);
    }

    public static AppException changeRequestNotFound(UUID id) {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_REQUEST_NOT_FOUND,
                "Change request not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException changeRequestCodeExists(String code) {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_REQUEST_CODE_ALREADY_EXISTS,
                "Change request code already exists: " + code, Map.of("code", code == null ? "" : code));
    }

    public static AppException changeRequestProjectArchived(UUID projectId) {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_REQUEST_PROJECT_ARCHIVED,
                "Cannot create change request for archived project: " + projectId, Map.of("projectId", projectId));
    }

    public static AppException changeRequestNotDraft(UUID id) {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_REQUEST_NOT_DRAFT,
                "Change request must be DRAFT: " + id, Map.of("id", id));
    }

    public static AppException changeRequestNotSubmitted(UUID id) {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_REQUEST_NOT_SUBMITTED,
                "Change request must be SUBMITTED: " + id, Map.of("id", id));
    }

    public static AppException changeRequestNotApproved(UUID id) {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_REQUEST_NOT_APPROVED,
                "Change request must be APPROVED: " + id, Map.of("id", id));
    }

    public static AppException changeRequestAlreadyApplied(UUID id) {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_REQUEST_ALREADY_APPLIED,
                "Change request already applied: " + id, Map.of("id", id));
    }

    public static AppException changeRequestItemRequired(UUID id) {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_REQUEST_ITEM_REQUIRED,
                "Change request requires items: " + id, Map.of("id", id));
    }

    public static AppException changeRequestImpactRequired(UUID id) {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_REQUEST_IMPACT_REQUIRED,
                "Change impact required: " + id, Map.of("id", id));
    }

    public static AppException rejectionReasonRequired() {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_REQUEST_REJECTION_REASON_REQUIRED);
    }

    public static AppException applyFailed(String message) {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_REQUEST_APPLY_FAILED, message);
    }

    public static AppException changeRequestAccessDenied() {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_REQUEST_ACCESS_DENIED);
    }

    public static AppException changeRequestInvalidStatus(UUID id, String action) {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_REQUEST_INVALID_STATUS,
                "Invalid change request status for " + action + ": " + id,
                Map.of("id", id, "action", action == null ? "" : action));
    }

    public static AppException itemNotFound(UUID id) {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_REQUEST_ITEM_NOT_FOUND,
                "Change request item not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException itemTargetMismatch(UUID targetId, UUID projectId) {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_REQUEST_ITEM_TARGET_MISMATCH,
                "Target does not belong to project",
                Map.of("targetId", targetId == null ? "" : targetId, "projectId", projectId));
    }

    public static AppException unsupportedOperation(String targetType, String operation) {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_REQUEST_ITEM_UNSUPPORTED_OPERATION,
                "Unsupported operation: " + targetType + "/" + operation,
                Map.of("targetType", targetType == null ? "" : targetType,
                        "operation", operation == null ? "" : operation));
    }

    public static AppException invalidPayload(String message) {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_REQUEST_ITEM_INVALID_PAYLOAD, message);
    }

    public static AppException impactNotFound(UUID changeRequestId) {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_IMPACT_NOT_FOUND,
                "Change impact not found for: " + changeRequestId, Map.of("changeRequestId", changeRequestId));
    }

    public static AppException changeOrderNotFound(UUID id) {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_ORDER_NOT_FOUND,
                "Change order not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException changeOrderCodeExists(String code) {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_ORDER_CODE_ALREADY_EXISTS,
                "Change order code already exists: " + code, Map.of("code", code == null ? "" : code));
    }

    public static AppException changeOrderNotDraft(UUID id) {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_ORDER_NOT_DRAFT,
                "Change order must be DRAFT: " + id, Map.of("id", id));
    }

    public static AppException changeOrderNotApprovable(UUID id) {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_ORDER_NOT_APPROVABLE,
                "Change order cannot be approved: " + id, Map.of("id", id));
    }

    public static AppException changeOrderInvalidSource(UUID changeRequestId) {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_ORDER_INVALID_SOURCE,
                "Change order requires APPROVED or APPLIED change request: " + changeRequestId,
                Map.of("changeRequestId", changeRequestId));
    }

    public static AppException changeOrderAccessDenied() {
        return new AppException(ProjectBaselineErrorCatalog.CHANGE_ORDER_ACCESS_DENIED);
    }

    public static AppException postBaselineEditBlocked(UUID projectId, String field) {
        return new AppException(ProjectBaselineErrorCatalog.POST_BASELINE_EDIT_BLOCKED,
                "Direct edit blocked after current baseline; create a ChangeRequest",
                Map.of("projectId", projectId, "field", field == null ? "" : field));
    }
}
