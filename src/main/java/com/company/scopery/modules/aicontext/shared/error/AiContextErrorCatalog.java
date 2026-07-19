package com.company.scopery.modules.aicontext.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum AiContextErrorCatalog implements ErrorCatalog {
    RESOLUTION_POLICY_NOT_FOUND("AI_CONTEXT_POLICY_NOT_FOUND", "AI context resolution policy not found", HttpStatus.NOT_FOUND),
    RESOLUTION_AUDIT_NOT_FOUND("AI_CONTEXT_AUDIT_NOT_FOUND", "AI context resolution audit record not found", HttpStatus.NOT_FOUND),
    RESOURCE_READ_DENIED("AI_RESOURCE_READ_DENIED", "AI_READ permission denied for resource", HttpStatus.FORBIDDEN),
    AI_PROCESSING_POLICY_BLOCKED("AI_PROCESSING_POLICY_BLOCKED", "AI processing is blocked by policy for this resource", HttpStatus.FORBIDDEN),
    CONTEXT_TOO_LARGE("AI_CONTEXT_TOO_LARGE", "AI context exceeds maximum allowed size", HttpStatus.PAYLOAD_TOO_LARGE),
    DOCUMENT_NOT_READABLE("AI_DOCUMENT_NOT_READABLE", "Document is not readable for AI context resolution", HttpStatus.FORBIDDEN);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    AiContextErrorCatalog(String c, String m, HttpStatus s) { code = c; defaultMessage = m; httpStatus = s; }

    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
