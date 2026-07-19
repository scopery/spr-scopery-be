package com.company.scopery.modules.traceability.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum TraceabilityErrorCatalog implements ErrorCatalog {
    REQUIREMENT_NOT_FOUND("REQUIREMENT_NOT_FOUND", "Requirement not found", HttpStatus.NOT_FOUND),
    REQUIREMENT_CODE_EXISTS("REQUIREMENT_CODE_ALREADY_EXISTS", "Requirement code already exists", HttpStatus.CONFLICT),
    REQUIREMENT_IMMUTABLE("REQUIREMENT_APPROVED_IMMUTABLE", "Approved requirement is immutable", HttpStatus.UNPROCESSABLE_ENTITY),
    REQUIREMENT_INVALID_STATUS("REQUIREMENT_INVALID_STATUS", "Invalid requirement status transition", HttpStatus.UNPROCESSABLE_ENTITY),
    REQUIREMENT_VERSION_NOT_FOUND("REQUIREMENT_VERSION_NOT_FOUND", "Requirement version not found", HttpStatus.NOT_FOUND),
    REQUIREMENT_SOURCE_NOT_FOUND("REQUIREMENT_SOURCE_NOT_FOUND", "Requirement source not found", HttpStatus.NOT_FOUND),
    REQUIREMENT_CRITERIA_NOT_FOUND("REQUIREMENT_CRITERIA_NOT_FOUND", "Requirement criteria not found", HttpStatus.NOT_FOUND),
    TRACE_LINK_NOT_FOUND("TRACE_LINK_NOT_FOUND", "Trace link not found", HttpStatus.NOT_FOUND),
    TRACE_LINK_DUPLICATE("TRACE_LINK_DUPLICATE", "Duplicate active trace link", HttpStatus.CONFLICT),
    APPLICATION_NOT_FOUND("APPLICATION_NOT_FOUND", "Application not found", HttpStatus.NOT_FOUND),
    APPLICATION_CODE_EXISTS("APPLICATION_CODE_ALREADY_EXISTS", "Application code already exists", HttpStatus.CONFLICT),
    APP_MODULE_NOT_FOUND("APPLICATION_MODULE_NOT_FOUND", "Application module not found", HttpStatus.NOT_FOUND),
    APP_MODULE_CODE_EXISTS("APPLICATION_MODULE_CODE_ALREADY_EXISTS", "Module code already exists", HttpStatus.CONFLICT),
    APP_COMPONENT_NOT_FOUND("APPLICATION_COMPONENT_NOT_FOUND", "Application component not found", HttpStatus.NOT_FOUND),
    APP_COMPONENT_CODE_EXISTS("APPLICATION_COMPONENT_CODE_ALREADY_EXISTS", "Component code already exists", HttpStatus.CONFLICT),
    SCREEN_NOT_FOUND("SCREEN_NOT_FOUND", "Screen not found", HttpStatus.NOT_FOUND),
    SCREEN_CODE_EXISTS("SCREEN_CODE_ALREADY_EXISTS", "Screen code already exists", HttpStatus.CONFLICT),
    SCREEN_SECTION_NOT_FOUND("SCREEN_SECTION_NOT_FOUND", "Screen section not found", HttpStatus.NOT_FOUND),
    SCREEN_FIELD_NOT_FOUND("SCREEN_FIELD_NOT_FOUND", "Screen field not found", HttpStatus.NOT_FOUND),
    SCREEN_FIELD_KEY_EXISTS("SCREEN_FIELD_KEY_ALREADY_EXISTS", "Screen field key already exists", HttpStatus.CONFLICT),
    SCREEN_ACTION_NOT_FOUND("SCREEN_ACTION_NOT_FOUND", "Screen action not found", HttpStatus.NOT_FOUND),
    SCREEN_ACTION_CODE_EXISTS("SCREEN_ACTION_CODE_ALREADY_EXISTS", "Screen action code already exists", HttpStatus.CONFLICT),
    API_ENDPOINT_NOT_FOUND("API_ENDPOINT_NOT_FOUND", "API endpoint not found", HttpStatus.NOT_FOUND),
    API_ENDPOINT_DUPLICATE("API_ENDPOINT_DUPLICATE", "Duplicate API endpoint method+path", HttpStatus.CONFLICT),
    DATA_ENTITY_NOT_FOUND("DATA_ENTITY_NOT_FOUND", "Data entity not found", HttpStatus.NOT_FOUND),
    DATA_ENTITY_CODE_EXISTS("DATA_ENTITY_CODE_ALREADY_EXISTS", "Data entity code already exists", HttpStatus.CONFLICT),
    ACCESS_DENIED("TRACEABILITY_ACCESS_DENIED", "Traceability access denied", HttpStatus.FORBIDDEN),
    PROJECT_ARCHIVED("TRACEABILITY_PROJECT_ARCHIVED", "Project archived", HttpStatus.UNPROCESSABLE_ENTITY),
    TITLE_REQUIRED("REQUIREMENT_TITLE_REQUIRED", "Title required", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    TraceabilityErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
