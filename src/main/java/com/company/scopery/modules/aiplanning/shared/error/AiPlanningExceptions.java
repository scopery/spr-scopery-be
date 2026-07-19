package com.company.scopery.modules.aiplanning.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class AiPlanningExceptions {
    private AiPlanningExceptions() {}

    public static AppException runNotFound(UUID id) {
        return new AppException(AiPlanningErrorCatalog.AI_PLANNING_RUN_NOT_FOUND,
                "AI planning run not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException runNotCancellable(UUID id) {
        return new AppException(AiPlanningErrorCatalog.AI_PLANNING_RUN_NOT_CANCELLABLE,
                "AI planning run cannot be cancelled: " + id, Map.of("id", id));
    }

    public static AppException suggestionNotFound(UUID id) {
        return new AppException(AiPlanningErrorCatalog.AI_PLANNING_SUGGESTION_NOT_FOUND,
                "AI planning suggestion not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException itemNotFound(UUID id) {
        return new AppException(AiPlanningErrorCatalog.AI_PLANNING_ITEM_NOT_FOUND,
                "AI planning suggestion item not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException invalidStatus(String detail) {
        return new AppException(AiPlanningErrorCatalog.AI_PLANNING_INVALID_STATUS, detail, Map.of());
    }

    public static AppException accessDenied() {
        return new AppException(AiPlanningErrorCatalog.AI_PLANNING_ACCESS_DENIED);
    }

    public static AppException applyNotAccepted(UUID id) {
        return new AppException(AiPlanningErrorCatalog.AI_PLANNING_APPLY_NOT_ACCEPTED,
                "Only accepted suggestions/items can be applied: " + id, Map.of("id", id));
    }

    public static AppException applyFailed(String detail) {
        return new AppException(AiPlanningErrorCatalog.AI_PLANNING_APPLY_FAILED, detail, Map.of());
    }

    public static AppException baselineRequiresChangeRequest(UUID projectId) {
        return new AppException(AiPlanningErrorCatalog.AI_PLANNING_BASELINE_REQUIRES_CHANGE_REQUEST,
                "Baselined project requires ChangeRequest", Map.of("projectId", projectId));
    }

    public static AppException rejectionReasonRequired() {
        return new AppException(AiPlanningErrorCatalog.AI_PLANNING_REJECTION_REASON_REQUIRED);
    }

    public static AppException invalidRunType(String runType) {
        return new AppException(AiPlanningErrorCatalog.AI_PLANNING_INVALID_RUN_TYPE,
                "Invalid run type: " + runType, Map.of("runType", runType == null ? "" : runType));
    }

    public static AppException projectArchived(UUID projectId) {
        return new AppException(AiPlanningErrorCatalog.AI_PLANNING_PROJECT_ARCHIVED,
                "Project archived: " + projectId, Map.of("projectId", projectId));
    }

    public static AppException financePermissionRequired() {
        return new AppException(AiPlanningErrorCatalog.AI_PLANNING_FINANCE_PERMISSION_REQUIRED);
    }

    public static AppException quotePermissionRequired() {
        return new AppException(AiPlanningErrorCatalog.AI_PLANNING_QUOTE_PERMISSION_REQUIRED);
    }
}
