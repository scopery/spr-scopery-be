package com.company.scopery.modules.knowledge.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class KnowledgeExceptions {

    private KnowledgeExceptions() {}

    public static AppException documentTypeNotFound(UUID id) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_NOT_FOUND,
                "Document type not found: " + id, Map.of("id", id));
    }

    public static AppException documentTypeCodeAlreadyExists(String code) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_CODE_ALREADY_EXISTS,
                "Document type code already exists: " + code, Map.of("code", code));
    }

    public static AppException documentTypeWorkspaceCodeAlreadyExists(String code, UUID workspaceId) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_WORKSPACE_CODE_ALREADY_EXISTS,
                "Document type code already exists in workspace " + workspaceId + ": " + code,
                Map.of("code", code, "workspaceId", workspaceId));
    }

    public static AppException documentTypeDeletedCannotBeModified(UUID id) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_DELETED_CANNOT_BE_MODIFIED,
                "Deleted document type cannot be modified: " + id, Map.of("id", id));
    }

    public static AppException documentTypeSystemCannotBeDeleted(String code) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_SYSTEM_CANNOT_BE_DELETED,
                "System document type cannot be deleted: " + code, Map.of("code", code));
    }

    public static AppException documentTypeSystemCannotBeModifiedByWorkspace(String code) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_SYSTEM_CANNOT_BE_MODIFIED_BY_WORKSPACE,
                "System document type cannot be modified via workspace API: " + code, Map.of("code", code));
    }

    public static AppException documentTypeWorkspaceScopeRequiresWorkspaceId() {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_WORKSPACE_SCOPE_REQUIRES_WORKSPACE_ID,
                KnowledgeErrorCatalog.DOCUMENT_TYPE_WORKSPACE_SCOPE_REQUIRES_WORKSPACE_ID.defaultMessage(), Map.of());
    }

    public static AppException documentTypeSystemScopeMustNotHaveWorkspaceId() {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_SYSTEM_SCOPE_MUST_NOT_HAVE_WORKSPACE_ID,
                KnowledgeErrorCatalog.DOCUMENT_TYPE_SYSTEM_SCOPE_MUST_NOT_HAVE_WORKSPACE_ID.defaultMessage(), Map.of());
    }
}
