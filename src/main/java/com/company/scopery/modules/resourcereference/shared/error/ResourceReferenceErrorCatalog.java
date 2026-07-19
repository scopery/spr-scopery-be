package com.company.scopery.modules.resourcereference.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum ResourceReferenceErrorCatalog implements ErrorCatalog {
    RESOURCE_TYPE_NOT_FOUND("RESOURCE_TYPE_NOT_FOUND", "Resource type not found", HttpStatus.NOT_FOUND),
    RESOURCE_TYPE_ALREADY_EXISTS("RESOURCE_TYPE_ALREADY_EXISTS", "Resource type already exists", HttpStatus.CONFLICT),
    RESOURCE_TYPE_DISABLED("RESOURCE_TYPE_DISABLED", "This resource type is not enabled", HttpStatus.UNPROCESSABLE_ENTITY),
    BATCH_RESOLVE_LIMIT_EXCEEDED("RESOURCE_BATCH_RESOLVE_LIMIT_EXCEEDED", "Batch resolve request exceeds maximum reference count", HttpStatus.UNPROCESSABLE_ENTITY),
    RESOLVE_ACCESS_DENIED("RESOURCE_RESOLVE_ACCESS_DENIED", "Access denied to one or more resources", HttpStatus.FORBIDDEN);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ResourceReferenceErrorCatalog(String c, String m, HttpStatus s) { code = c; defaultMessage = m; httpStatus = s; }

    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
