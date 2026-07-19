package com.company.scopery.modules.scope.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class ScopeExceptions {
    private ScopeExceptions() {}

    public static AppException packageNotFound(UUID id) {
        return new AppException(ScopeErrorCatalog.SCOPE_PACKAGE_NOT_FOUND, "Scope package not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException codeExists(String code) {
        return new AppException(ScopeErrorCatalog.SCOPE_PACKAGE_CODE_EXISTS, "Code exists: " + code, Map.of("code", code == null ? "" : code));
    }

    public static AppException packageImmutable(UUID id) {
        return new AppException(ScopeErrorCatalog.SCOPE_PACKAGE_IMMUTABLE, "Package immutable: " + id, Map.of("id", id));
    }

    public static AppException itemNotFound(UUID id) {
        return new AppException(ScopeErrorCatalog.SCOPE_ITEM_NOT_FOUND, "Scope item not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException itemTitleRequired() {
        return new AppException(ScopeErrorCatalog.SCOPE_ITEM_TITLE_REQUIRED);
    }

    public static AppException invalidFlags() {
        return new AppException(ScopeErrorCatalog.SCOPE_ITEM_INVALID_FLAGS);
    }

    public static AppException deliverableNotFound(UUID id) {
        return new AppException(ScopeErrorCatalog.DELIVERABLE_NOT_FOUND, "Deliverable not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException invalidStatus(String d) {
        return new AppException(ScopeErrorCatalog.DELIVERABLE_INVALID_STATUS, d, Map.of());
    }

    public static AppException criteriaNotMet(UUID id) {
        return new AppException(ScopeErrorCatalog.DELIVERABLE_CRITERIA_NOT_MET, "Criteria not met: " + id, Map.of("id", id));
    }

    public static AppException reopenReasonRequired() {
        return new AppException(ScopeErrorCatalog.DELIVERABLE_REOPEN_REASON_REQUIRED);
    }

    public static AppException criteriaNotFound(UUID id) {
        return new AppException(ScopeErrorCatalog.ACCEPTANCE_CRITERIA_NOT_FOUND, "Acceptance criteria not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException criteriaTitleRequired() {
        return new AppException(ScopeErrorCatalog.ACCEPTANCE_CRITERIA_TITLE_REQUIRED);
    }

    public static AppException accessDenied() {
        return new AppException(ScopeErrorCatalog.SCOPE_ACCESS_DENIED);
    }

    public static AppException projectArchived(UUID id) {
        return new AppException(ScopeErrorCatalog.SCOPE_PROJECT_ARCHIVED, "Project archived: " + id, Map.of("projectId", id));
    }

    public static AppException evidenceNotFound(UUID id) {
        return new AppException(ScopeErrorCatalog.EVIDENCE_NOT_FOUND, "Evidence not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException reviewNotFound(UUID id) {
        return new AppException(ScopeErrorCatalog.REVIEW_NOT_FOUND, "Review not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException reviewInvalidStatus(String detail) {
        return new AppException(ScopeErrorCatalog.REVIEW_INVALID_STATUS, detail, Map.of());
    }

    public static AppException wbsMappingNotFound(UUID id) {
        return new AppException(ScopeErrorCatalog.WBS_MAPPING_NOT_FOUND, "WBS mapping not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException taskMappingNotFound(UUID id) {
        return new AppException(ScopeErrorCatalog.TASK_MAPPING_NOT_FOUND, "Task mapping not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException wbsNodeNotFound(UUID id) {
        return new AppException(ScopeErrorCatalog.WBS_NODE_NOT_FOUND, "WBS node not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException taskNotFound(UUID id) {
        return new AppException(ScopeErrorCatalog.TASK_NOT_FOUND, "Task not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException quoteVersionNotFound(UUID id) {
        return new AppException(ScopeErrorCatalog.QUOTE_VERSION_NOT_FOUND, "Quote version not found: " + id, Map.of("id", id == null ? "" : id));
    }

    public static AppException quoteVersionProjectMismatch(UUID quoteVersionId, UUID projectId) {
        return new AppException(ScopeErrorCatalog.QUOTE_VERSION_PROJECT_MISMATCH,
                "Quote version " + quoteVersionId + " does not belong to project " + projectId,
                Map.of("quoteVersionId", quoteVersionId, "projectId", projectId));
    }

    public static AppException packageNotApproved(UUID id) {
        return new AppException(ScopeErrorCatalog.PACKAGE_NOT_APPROVED, "Package not approved: " + id, Map.of("id", id));
    }

    public static AppException openReviewExists(UUID deliverableId) {
        return new AppException(ScopeErrorCatalog.DELIVERABLE_OPEN_REVIEW_EXISTS,
                "Open review exists for deliverable: " + deliverableId, Map.of("deliverableId", deliverableId));
    }
}
