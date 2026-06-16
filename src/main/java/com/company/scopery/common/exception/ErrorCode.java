package com.company.scopery.common.exception;

public final class ErrorCode {

    public static final String INTERNAL_SERVER_ERROR  = "INTERNAL_SERVER_ERROR";
    public static final String VALIDATION_ERROR       = "VALIDATION_ERROR";
    public static final String RESOURCE_NOT_FOUND     = "RESOURCE_NOT_FOUND";
    public static final String RESOURCE_CONFLICT      = "RESOURCE_CONFLICT";
    public static final String BUSINESS_RULE_VIOLATION = "BUSINESS_RULE_VIOLATION";
    public static final String INVALID_REQUEST        = "INVALID_REQUEST";

    private ErrorCode() {}
}