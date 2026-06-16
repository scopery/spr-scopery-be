package com.company.scopery.common.exception;

import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;

public class AppException extends RuntimeException {

    private final ErrorCatalog errorCatalog;
    private final Map<String, Object> details;

    public AppException(ErrorCatalog errorCatalog) {
        super(errorCatalog.defaultMessage());
        this.errorCatalog = errorCatalog;
        this.details = Map.of();
    }

    public AppException(ErrorCatalog errorCatalog, String message) {
        super(message != null ? message : errorCatalog.defaultMessage());
        this.errorCatalog = errorCatalog;
        this.details = Map.of();
    }

    public AppException(ErrorCatalog errorCatalog, String message, Map<String, Object> details) {
        super(message != null ? message : errorCatalog.defaultMessage());
        this.errorCatalog = errorCatalog;
        this.details = details != null ? Collections.unmodifiableMap(details) : Map.of();
    }

    public AppException(ErrorCatalog errorCatalog, Throwable cause) {
        super(errorCatalog.defaultMessage(), cause);
        this.errorCatalog = errorCatalog;
        this.details = Map.of();
    }

    public String getErrorCode() {
        return errorCatalog.code();
    }

    public HttpStatus getHttpStatus() {
        return errorCatalog.httpStatus();
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}