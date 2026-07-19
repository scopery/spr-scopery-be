package com.company.scopery.modules.eventregistry.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum EventRegistryErrorCatalog implements ErrorCatalog {

    EVENT_DEFINITION_NOT_FOUND(
            "EVENT_DEFINITION_NOT_FOUND",
            "Event definition not found",
            HttpStatus.NOT_FOUND),

    EVENT_DEFINITION_CODE_ALREADY_EXISTS(
            "EVENT_DEFINITION_CODE_ALREADY_EXISTS",
            "Event definition code already exists",
            HttpStatus.CONFLICT),

    EVENT_DEFINITION_SOURCE_EVENT_ALREADY_EXISTS(
            "EVENT_DEFINITION_SOURCE_EVENT_ALREADY_EXISTS",
            "This sourceSystem + eventKey combination already exists",
            HttpStatus.CONFLICT),

    DEPRECATED_EVENT_DEFINITION_CANNOT_BE_ACTIVATED(
            "DEPRECATED_EVENT_DEFINITION_CANNOT_BE_ACTIVATED",
            "Deprecated event definition cannot be activated again",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EVENT_DEFINITION_ALREADY_DEPRECATED(
            "EVENT_DEFINITION_ALREADY_DEPRECATED",
            "Event definition is already deprecated",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EVENT_DEFINITION_DEPRECATED_CANNOT_BE_UPDATED(
            "EVENT_DEFINITION_DEPRECATED_CANNOT_BE_UPDATED",
            "Deprecated event definition cannot be updated",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EVENT_DEFINITION_HAS_ACTIVE_CONSUMERS(
            "EVENT_DEFINITION_HAS_ACTIVE_CONSUMERS",
            "Event definition has active consumers and cannot be changed unsafely",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EVENT_VARIABLE_INVALID_PATH(
            "EVENT_VARIABLE_INVALID_PATH",
            "Event variable path is invalid",
            HttpStatus.BAD_REQUEST),

    EVENT_VARIABLE_DUPLICATE_PATH(
            "EVENT_VARIABLE_DUPLICATE_PATH",
            "Duplicate event variable path in request",
            HttpStatus.BAD_REQUEST),

    EVENT_VARIABLE_REQUIRED_REMOVAL_BLOCKED(
            "EVENT_VARIABLE_REQUIRED_REMOVAL_BLOCKED",
            "Removing a required variable used by active consumers is blocked",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EVENT_VARIABLE_TYPE_CHANGE_BLOCKED(
            "EVENT_VARIABLE_TYPE_CHANGE_BLOCKED",
            "Changing type of a variable used by active consumers is blocked",
            HttpStatus.UNPROCESSABLE_ENTITY),

    INVALID_EVENT_DEFINITION_STATUS(
            "INVALID_EVENT_DEFINITION_STATUS",
            "Invalid event definition status",
            HttpStatus.BAD_REQUEST),

    INVALID_EVENT_DEFINITION_DATA_CLASSIFICATION(
            "INVALID_EVENT_DEFINITION_DATA_CLASSIFICATION",
            "Invalid event data classification",
            HttpStatus.BAD_REQUEST),

    INVALID_EVENT_DEFINITION_INPUT_SCHEMA_JSON(
            "INVALID_EVENT_DEFINITION_INPUT_SCHEMA_JSON",
            "Input schema is not valid JSON",
            HttpStatus.BAD_REQUEST),

    INVALID_EVENT_DEFINITION_OUTPUT_SCHEMA_JSON(
            "INVALID_EVENT_DEFINITION_OUTPUT_SCHEMA_JSON",
            "Output schema is not valid JSON",
            HttpStatus.BAD_REQUEST),

    INVALID_EVENT_VARIABLE_TYPE(
            "INVALID_EVENT_VARIABLE_TYPE",
            "Invalid event variable type",
            HttpStatus.BAD_REQUEST),

    EVENT_REGISTRY_ACCESS_DENIED(
            "EVENT_REGISTRY_ACCESS_DENIED",
            "Access to event registry is denied",
            HttpStatus.FORBIDDEN);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    EventRegistryErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override
    public String code() {
        return code;
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
