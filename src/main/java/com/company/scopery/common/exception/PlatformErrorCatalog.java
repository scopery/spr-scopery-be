package com.company.scopery.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Platform reliability error catalog (Phase 04).
 */
public enum PlatformErrorCatalog implements ErrorCatalog {

    PLATFORM_EVENT_DEFINITION_NOT_FOUND("Event definition was not found", HttpStatus.NOT_FOUND),
    PLATFORM_OUTBOX_MESSAGE_NOT_FOUND("Outbox message was not found", HttpStatus.NOT_FOUND),
    PLATFORM_OUTBOX_MESSAGE_NOT_RETRYABLE("Outbox message is not retryable", HttpStatus.UNPROCESSABLE_ENTITY),
    PLATFORM_OUTBOX_MESSAGE_ALREADY_PUBLISHED("Outbox message is already published", HttpStatus.CONFLICT),
    PLATFORM_OUTBOX_MAX_ATTEMPTS_EXCEEDED("Outbox message exceeded max attempts", HttpStatus.UNPROCESSABLE_ENTITY),
    PLATFORM_IDEMPOTENCY_CONFLICT("Idempotency key reused with a different request body", HttpStatus.CONFLICT),
    PLATFORM_IDEMPOTENCY_IN_PROGRESS("Idempotency key is already being processed", HttpStatus.CONFLICT),
    PLATFORM_IDEMPOTENCY_RECORD_NOT_FOUND("Idempotency record was not found", HttpStatus.NOT_FOUND),
    PLATFORM_AUDIT_WRITE_FAILED("Immutable audit write failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PLATFORM_ACTIVITY_WRITE_FAILED("Activity log write failed", HttpStatus.INTERNAL_SERVER_ERROR),
    PLATFORM_TRACE_ID_INVALID("Trace ID is invalid", HttpStatus.BAD_REQUEST),
    PLATFORM_JOB_LOCK_FAILED("Job lock could not be acquired", HttpStatus.CONFLICT);

    private final String defaultMessage;
    private final HttpStatus httpStatus;

    PlatformErrorCatalog(String defaultMessage, HttpStatus httpStatus) {
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override
    public String code() {
        return name();
    }

    @Override
    public String defaultMessage() {
        return defaultMessage;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
