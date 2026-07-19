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

    public static AppException documentTypeOrganizationCodeAlreadyExists(String code, UUID organizationId) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_ORGANIZATION_CODE_ALREADY_EXISTS,
                "Document type code already exists in organization " + organizationId + ": " + code,
                Map.of("code", code, "organizationId", organizationId));
    }

    public static AppException documentTypeArchivedCannotBeModified(UUID id) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_ARCHIVED,
                "Archived document type cannot be modified: " + id, Map.of("id", id));
    }

    /** @deprecated Prefer {@link #documentTypeArchivedCannotBeModified(UUID)}. */
    @Deprecated
    public static AppException documentTypeDeletedCannotBeModified(UUID id) {
        return documentTypeArchivedCannotBeModified(id);
    }

    public static AppException documentTypeBuiltInCannotDelete(String code) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_BUILT_IN_CANNOT_DELETE,
                "Built-in document type cannot be archived or deleted: " + code, Map.of("code", code));
    }

    /** @deprecated Prefer {@link #documentTypeBuiltInCannotDelete(String)}. */
    @Deprecated
    public static AppException documentTypeSystemCannotBeDeleted(String code) {
        return documentTypeBuiltInCannotDelete(code);
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

    public static AppException documentTypeOrganizationScopeRequiresOrganizationId() {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_ORGANIZATION_SCOPE_REQUIRES_ORGANIZATION_ID,
                KnowledgeErrorCatalog.DOCUMENT_TYPE_ORGANIZATION_SCOPE_REQUIRES_ORGANIZATION_ID.defaultMessage(), Map.of());
    }

    public static AppException documentTypeInvalidScope(String message) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_INVALID_SCOPE, message, Map.of());
    }

    public static AppException documentTypeInvalidClassification(String value) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_INVALID_CLASSIFICATION,
                "Invalid classification: " + value, Map.of("value", value == null ? "" : value));
    }

    public static AppException documentTypeInvalidMetadataSchema() {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_INVALID_METADATA_SCHEMA,
                KnowledgeErrorCatalog.DOCUMENT_TYPE_INVALID_METADATA_SCHEMA.defaultMessage(), Map.of());
    }

    public static AppException documentTypeFieldNotFound(UUID id) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_FIELD_NOT_FOUND,
                "Document type field not found: " + id, Map.of("id", id));
    }

    public static AppException documentTypeFieldKeyAlreadyExists(String fieldKey) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_FIELD_CODE_ALREADY_EXISTS,
                "Field key already exists: " + fieldKey, Map.of("fieldKey", fieldKey));
    }

    public static AppException documentTypeFieldInvalidKey(String message) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_FIELD_INVALID_KEY, message, Map.of());
    }

    public static AppException documentTypeFieldInvalidDataType(String value) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_FIELD_INVALID_DATA_TYPE,
                "Invalid field data type: " + value, Map.of("value", value == null ? "" : value));
    }

    public static AppException documentTypeFieldOptionsRequired() {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_FIELD_OPTIONS_REQUIRED,
                KnowledgeErrorCatalog.DOCUMENT_TYPE_FIELD_OPTIONS_REQUIRED.defaultMessage(), Map.of());
    }

    public static AppException documentTypeFieldSystemCannotArchive(String fieldKey) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_FIELD_SYSTEM_CANNOT_ARCHIVE,
                "System field cannot be archived: " + fieldKey, Map.of("fieldKey", fieldKey));
    }

    public static AppException documentTypeFieldPathMismatch(UUID documentTypeId, UUID fieldId) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_FIELD_PATH_MISMATCH,
                "Field " + fieldId + " does not belong to document type " + documentTypeId,
                Map.of("documentTypeId", documentTypeId, "fieldId", fieldId));
    }

    public static AppException documentTypeFieldReorderInvalid(String message) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_FIELD_REORDER_INVALID, message, Map.of());
    }

    public static AppException documentTypeFieldArchived(UUID id) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_TYPE_FIELD_ARCHIVED,
                "Archived document type field cannot be modified: " + id, Map.of("id", id));
    }

    // Phase 41 — semantic retrieval + storage
    public static AppException knowledgeQueryInvalid(String reason) {
        return new AppException(KnowledgeErrorCatalog.KNOWLEDGE_QUERY_INVALID, reason, Map.of());
    }

    public static AppException knowledgeSourceTypeUnsupported(String sourceType) {
        return new AppException(KnowledgeErrorCatalog.KNOWLEDGE_SOURCE_TYPE_UNSUPPORTED,
                "Unsupported source type: " + sourceType, Map.of("sourceType", sourceType));
    }

    public static AppException knowledgeSourceNotFound(UUID id) {
        return new AppException(KnowledgeErrorCatalog.KNOWLEDGE_SOURCE_NOT_FOUND,
                "Knowledge source not found: " + id, Map.of("id", id));
    }

    public static AppException knowledgeSourceAccessDenied(UUID id) {
        return new AppException(KnowledgeErrorCatalog.KNOWLEDGE_SOURCE_ACCESS_DENIED,
                "Access denied for knowledge source: " + id, Map.of("id", id));
    }

    public static AppException knowledgeRetrievalAccessDenied() {
        return new AppException(KnowledgeErrorCatalog.KNOWLEDGE_RETRIEVAL_ACCESS_DENIED,
                KnowledgeErrorCatalog.KNOWLEDGE_RETRIEVAL_ACCESS_DENIED.defaultMessage(), Map.of());
    }

    public static AppException knowledgeRetrievalProviderUnavailable(String provider) {
        return new AppException(KnowledgeErrorCatalog.KNOWLEDGE_RETRIEVAL_PROVIDER_UNAVAILABLE,
                "Retrieval provider unavailable: " + provider, Map.of("provider", provider));
    }

    public static AppException knowledgeIndexJobNotFound(UUID jobId) {
        return new AppException(KnowledgeErrorCatalog.KNOWLEDGE_INDEX_JOB_NOT_FOUND,
                "Index job not found: " + jobId, Map.of("jobId", jobId));
    }

    public static AppException knowledgeIndexJobConflict(String idempotencyKey) {
        return new AppException(KnowledgeErrorCatalog.KNOWLEDGE_INDEX_JOB_CONFLICT,
                "Index job already in progress", Map.of("idempotencyKey", idempotencyKey));
    }

    public static AppException knowledgeGraphNodeNotFound(UUID nodeId) {
        return new AppException(KnowledgeErrorCatalog.KNOWLEDGE_GRAPH_NODE_NOT_FOUND,
                "Knowledge graph node not found: " + nodeId, Map.of("nodeId", nodeId));
    }

    public static AppException knowledgeGraphLimitInvalid(String reason) {
        return new AppException(KnowledgeErrorCatalog.KNOWLEDGE_GRAPH_LIMIT_INVALID, reason, Map.of());
    }

    public static AppException knowledgeExtractionUnsupported(String contentType) {
        return new AppException(KnowledgeErrorCatalog.KNOWLEDGE_EXTRACTION_UNSUPPORTED,
                "Content type not supported for extraction: " + contentType, Map.of("contentType", contentType));
    }

    public static AppException knowledgeExtractionEmptyOrScanned() {
        return new AppException(KnowledgeErrorCatalog.KNOWLEDGE_EXTRACTION_EMPTY_OR_SCANNED,
                KnowledgeErrorCatalog.KNOWLEDGE_EXTRACTION_EMPTY_OR_SCANNED.defaultMessage(), Map.of());
    }

    public static AppException documentStorageUploadNotFound(UUID versionId) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_STORAGE_UPLOAD_NOT_FOUND,
                "Presigned upload not found for version: " + versionId, Map.of("versionId", versionId));
    }

    public static AppException documentStorageUploadNotComplete(String storageKey) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_STORAGE_UPLOAD_NOT_COMPLETE,
                "Object not found in storage after upload", Map.of("storageKey", storageKey));
    }

    public static AppException documentStorageObjectMismatch(String reason) {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_STORAGE_OBJECT_MISMATCH, reason, Map.of());
    }

    public static AppException documentStorageProviderUnavailable() {
        return new AppException(KnowledgeErrorCatalog.DOCUMENT_STORAGE_PROVIDER_UNAVAILABLE,
                KnowledgeErrorCatalog.DOCUMENT_STORAGE_PROVIDER_UNAVAILABLE.defaultMessage(), Map.of());
    }
}
