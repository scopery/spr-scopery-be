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
    DOCUMENT_TYPE_DELETED(
            "DOCUMENT_TYPE_DELETED", "Document type has been deleted", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENT_TYPE_DELETED_CANNOT_BE_MODIFIED(
            "DOCUMENT_TYPE_DELETED_CANNOT_BE_MODIFIED", "Deleted document type cannot be modified", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENT_TYPE_SYSTEM_CANNOT_BE_DELETED(
            "DOCUMENT_TYPE_SYSTEM_CANNOT_BE_DELETED", "System document types cannot be deleted", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENT_TYPE_SYSTEM_CANNOT_BE_MODIFIED_BY_WORKSPACE(
            "DOCUMENT_TYPE_SYSTEM_CANNOT_BE_MODIFIED_BY_WORKSPACE", "System document types cannot be modified via workspace API", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENT_TYPE_WORKSPACE_SCOPE_REQUIRES_WORKSPACE_ID(
            "DOCUMENT_TYPE_WORKSPACE_SCOPE_REQUIRES_WORKSPACE_ID", "Workspace-scoped document type requires a workspaceId", HttpStatus.BAD_REQUEST),
    DOCUMENT_TYPE_SYSTEM_SCOPE_MUST_NOT_HAVE_WORKSPACE_ID(
            "DOCUMENT_TYPE_SYSTEM_SCOPE_MUST_NOT_HAVE_WORKSPACE_ID", "System-scoped document type must not have a workspaceId", HttpStatus.BAD_REQUEST),
    INVALID_DOCUMENT_TYPE_STATUS(
            "INVALID_DOCUMENT_TYPE_STATUS", "Invalid document type status value", HttpStatus.BAD_REQUEST),
    INVALID_DOCUMENT_TYPE_SCOPE(
            "INVALID_DOCUMENT_TYPE_SCOPE", "Invalid document type scope value", HttpStatus.BAD_REQUEST),
    DOCUMENT_TYPE_INACTIVE(
            "DOCUMENT_TYPE_INACTIVE", "Document type is inactive and cannot be used", HttpStatus.UNPROCESSABLE_ENTITY);

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
