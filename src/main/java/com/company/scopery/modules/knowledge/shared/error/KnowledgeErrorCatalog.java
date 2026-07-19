package com.company.scopery.modules.knowledge.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum KnowledgeErrorCatalog implements ErrorCatalog {

    // Document Type
    DOCUMENT_TYPE_NOT_FOUND(
            "DOCUMENT_TYPE_NOT_FOUND", "Document type not found", HttpStatus.NOT_FOUND),
    DOCUMENT_TYPE_CODE_ALREADY_EXISTS(
            "DOCUMENT_TYPE_CODE_ALREADY_EXISTS", "Document type code already exists", HttpStatus.CONFLICT),
    DOCUMENT_TYPE_WORKSPACE_CODE_ALREADY_EXISTS(
            "DOCUMENT_TYPE_WORKSPACE_CODE_ALREADY_EXISTS", "Document type code already exists in this workspace", HttpStatus.CONFLICT),
    DOCUMENT_TYPE_ORGANIZATION_CODE_ALREADY_EXISTS(
            "DOCUMENT_TYPE_ORGANIZATION_CODE_ALREADY_EXISTS", "Document type code already exists in this organization", HttpStatus.CONFLICT),
    DOCUMENT_TYPE_DELETED(
            "DOCUMENT_TYPE_ARCHIVED", "Document type has been archived", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENT_TYPE_DELETED_CANNOT_BE_MODIFIED(
            "DOCUMENT_TYPE_ARCHIVED", "Archived document type cannot be modified", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENT_TYPE_ARCHIVED(
            "DOCUMENT_TYPE_ARCHIVED", "Document type has been archived", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENT_TYPE_SYSTEM_CANNOT_BE_DELETED(
            "DOCUMENT_TYPE_BUILT_IN_CANNOT_DELETE", "Built-in/system document types cannot be archived or deleted", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENT_TYPE_BUILT_IN_CANNOT_DELETE(
            "DOCUMENT_TYPE_BUILT_IN_CANNOT_DELETE", "Built-in document types cannot be archived or deleted", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENT_TYPE_SYSTEM_CANNOT_BE_MODIFIED_BY_WORKSPACE(
            "DOCUMENT_TYPE_SYSTEM_CANNOT_BE_MODIFIED_BY_WORKSPACE", "System document types cannot be modified via workspace API", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENT_TYPE_WORKSPACE_SCOPE_REQUIRES_WORKSPACE_ID(
            "DOCUMENT_TYPE_WORKSPACE_SCOPE_REQUIRES_WORKSPACE_ID", "Workspace-scoped document type requires a workspaceId", HttpStatus.BAD_REQUEST),
    DOCUMENT_TYPE_SYSTEM_SCOPE_MUST_NOT_HAVE_WORKSPACE_ID(
            "DOCUMENT_TYPE_SYSTEM_SCOPE_MUST_NOT_HAVE_WORKSPACE_ID", "System-scoped document type must not have a workspaceId", HttpStatus.BAD_REQUEST),
    DOCUMENT_TYPE_ORGANIZATION_SCOPE_REQUIRES_ORGANIZATION_ID(
            "DOCUMENT_TYPE_INVALID_SCOPE", "Organization-scoped document type requires an organizationId", HttpStatus.BAD_REQUEST),
    DOCUMENT_TYPE_INVALID_SCOPE(
            "DOCUMENT_TYPE_INVALID_SCOPE", "Invalid document type scope for the provided identifiers", HttpStatus.BAD_REQUEST),
    DOCUMENT_TYPE_INVALID_CLASSIFICATION(
            "DOCUMENT_TYPE_INVALID_CLASSIFICATION", "Invalid document classification value", HttpStatus.BAD_REQUEST),
    DOCUMENT_TYPE_INVALID_METADATA_SCHEMA(
            "DOCUMENT_TYPE_INVALID_METADATA_SCHEMA", "metadataSchemaJson must be valid JSON", HttpStatus.BAD_REQUEST),
    INVALID_DOCUMENT_TYPE_STATUS(
            "INVALID_DOCUMENT_TYPE_STATUS", "Invalid document type status value", HttpStatus.BAD_REQUEST),
    INVALID_DOCUMENT_TYPE_SCOPE(
            "INVALID_DOCUMENT_TYPE_SCOPE", "Invalid document type scope value", HttpStatus.BAD_REQUEST),
    DOCUMENT_TYPE_INACTIVE(
            "DOCUMENT_TYPE_INACTIVE", "Document type is inactive and cannot be used", HttpStatus.UNPROCESSABLE_ENTITY),

    // Document Type Field
    DOCUMENT_TYPE_FIELD_NOT_FOUND(
            "DOCUMENT_TYPE_FIELD_NOT_FOUND", "Document type field not found", HttpStatus.NOT_FOUND),
    DOCUMENT_TYPE_FIELD_CODE_ALREADY_EXISTS(
            "DOCUMENT_TYPE_FIELD_CODE_ALREADY_EXISTS", "Field key already exists on this document type", HttpStatus.CONFLICT),
    DOCUMENT_TYPE_FIELD_INVALID_KEY(
            "DOCUMENT_TYPE_FIELD_INVALID_KEY", "Invalid document type field key", HttpStatus.BAD_REQUEST),
    DOCUMENT_TYPE_FIELD_INVALID_DATA_TYPE(
            "DOCUMENT_TYPE_FIELD_INVALID_DATA_TYPE", "Invalid document type field data type", HttpStatus.BAD_REQUEST),
    DOCUMENT_TYPE_FIELD_OPTIONS_REQUIRED(
            "DOCUMENT_TYPE_FIELD_OPTIONS_REQUIRED", "SELECT/MULTI_SELECT fields require optionsJson", HttpStatus.BAD_REQUEST),
    DOCUMENT_TYPE_FIELD_SYSTEM_CANNOT_ARCHIVE(
            "DOCUMENT_TYPE_FIELD_SYSTEM_CANNOT_ARCHIVE", "System fields cannot be archived", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENT_TYPE_FIELD_PATH_MISMATCH(
            "DOCUMENT_TYPE_FIELD_PATH_MISMATCH", "Field does not belong to the specified document type", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENT_TYPE_FIELD_REORDER_INVALID(
            "DOCUMENT_TYPE_FIELD_REORDER_INVALID", "Reorder payload is invalid for this document type", HttpStatus.BAD_REQUEST),
    DOCUMENT_TYPE_FIELD_ARCHIVED(
            "DOCUMENT_TYPE_FIELD_ARCHIVED", "Archived document type field cannot be modified", HttpStatus.UNPROCESSABLE_ENTITY),

    KNOWLEDGE_ACCESS_DENIED(
            "KNOWLEDGE_ACCESS_DENIED", "Access denied for knowledge resource", HttpStatus.FORBIDDEN),

    // Phase 41 — semantic retrieval errors
    KNOWLEDGE_QUERY_INVALID(
            "KNOWLEDGE_QUERY_INVALID", "Search query is invalid or empty", HttpStatus.BAD_REQUEST),
    KNOWLEDGE_SOURCE_TYPE_UNSUPPORTED(
            "KNOWLEDGE_SOURCE_TYPE_UNSUPPORTED", "Unsupported knowledge source type", HttpStatus.BAD_REQUEST),
    KNOWLEDGE_SOURCE_NOT_FOUND(
            "KNOWLEDGE_SOURCE_NOT_FOUND", "Knowledge source not found", HttpStatus.NOT_FOUND),
    KNOWLEDGE_SOURCE_ACCESS_DENIED(
            "KNOWLEDGE_SOURCE_ACCESS_DENIED", "Access denied for knowledge source", HttpStatus.FORBIDDEN),
    KNOWLEDGE_CHUNK_CONTENT_ACCESS_DENIED(
            "KNOWLEDGE_CHUNK_CONTENT_ACCESS_DENIED", "Access denied for chunk content", HttpStatus.FORBIDDEN),
    KNOWLEDGE_RETRIEVAL_ACCESS_DENIED(
            "KNOWLEDGE_RETRIEVAL_ACCESS_DENIED", "Access denied for knowledge retrieval", HttpStatus.FORBIDDEN),
    KNOWLEDGE_RETRIEVAL_PROVIDER_UNAVAILABLE(
            "KNOWLEDGE_RETRIEVAL_PROVIDER_UNAVAILABLE", "Retrieval provider is temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    KNOWLEDGE_RETRIEVAL_TIMEOUT(
            "KNOWLEDGE_RETRIEVAL_TIMEOUT", "Retrieval request timed out", HttpStatus.GATEWAY_TIMEOUT),
    KNOWLEDGE_INDEX_JOB_NOT_FOUND(
            "KNOWLEDGE_INDEX_JOB_NOT_FOUND", "Index job not found", HttpStatus.NOT_FOUND),
    KNOWLEDGE_INDEX_JOB_CONFLICT(
            "KNOWLEDGE_INDEX_JOB_CONFLICT", "An index job for this source is already in progress", HttpStatus.CONFLICT),
    KNOWLEDGE_GRAPH_NODE_NOT_FOUND(
            "KNOWLEDGE_GRAPH_NODE_NOT_FOUND", "Knowledge graph node not found", HttpStatus.NOT_FOUND),
    KNOWLEDGE_GRAPH_LIMIT_INVALID(
            "KNOWLEDGE_GRAPH_LIMIT_INVALID", "Graph traversal depth or limit is invalid", HttpStatus.BAD_REQUEST),
    KNOWLEDGE_EXTRACTION_UNSUPPORTED(
            "KNOWLEDGE_EXTRACTION_UNSUPPORTED", "Document content type is not supported for text extraction", HttpStatus.UNPROCESSABLE_ENTITY),
    KNOWLEDGE_EXTRACTION_EMPTY_OR_SCANNED(
            "KNOWLEDGE_EXTRACTION_EMPTY_OR_SCANNED", "Document appears to be a scanned image; no extractable text found", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENT_STORAGE_UPLOAD_NOT_FOUND(
            "DOCUMENT_STORAGE_UPLOAD_NOT_FOUND", "Presigned upload not found or already completed", HttpStatus.NOT_FOUND),
    DOCUMENT_STORAGE_UPLOAD_NOT_COMPLETE(
            "DOCUMENT_STORAGE_UPLOAD_NOT_COMPLETE", "Object was not found in storage; upload may not have completed", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENT_STORAGE_OBJECT_MISMATCH(
            "DOCUMENT_STORAGE_OBJECT_MISMATCH", "Stored object does not match the expected checksum or size", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENT_STORAGE_PROVIDER_UNAVAILABLE(
            "DOCUMENT_STORAGE_PROVIDER_UNAVAILABLE", "Object storage provider is temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    KnowledgeErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
