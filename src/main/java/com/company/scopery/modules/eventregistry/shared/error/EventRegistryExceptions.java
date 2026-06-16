package com.company.scopery.modules.eventregistry.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class EventRegistryExceptions {

    private EventRegistryExceptions() {}

    public static AppException eventDefinitionNotFound(UUID id) {
        return new AppException(EventRegistryErrorCatalog.EVENT_DEFINITION_NOT_FOUND,
                "Event definition not found: " + id, Map.of("id", id));
    }

    public static AppException eventDefinitionCodeAlreadyExists(String code) {
        return new AppException(EventRegistryErrorCatalog.EVENT_DEFINITION_CODE_ALREADY_EXISTS,
                "Event definition code already exists: " + code, Map.of("code", code));
    }

    public static AppException eventDefinitionSourceEventAlreadyExists(String sourceSystem, String eventKey) {
        return new AppException(EventRegistryErrorCatalog.EVENT_DEFINITION_SOURCE_EVENT_ALREADY_EXISTS,
                "Event definition already exists for sourceSystem=" + sourceSystem + " eventKey=" + eventKey,
                Map.of("sourceSystem", sourceSystem, "eventKey", eventKey));
    }

    public static AppException deprecatedEventDefinitionCannotBeActivated(String code) {
        return new AppException(EventRegistryErrorCatalog.DEPRECATED_EVENT_DEFINITION_CANNOT_BE_ACTIVATED,
                "Deprecated event definition cannot be activated again: " + code, Map.of("code", code));
    }

    public static AppException invalidInputSchemaJson() {
        return new AppException(EventRegistryErrorCatalog.INVALID_EVENT_DEFINITION_INPUT_SCHEMA_JSON);
    }

    public static AppException invalidOutputSchemaJson() {
        return new AppException(EventRegistryErrorCatalog.INVALID_EVENT_DEFINITION_OUTPUT_SCHEMA_JSON);
    }
}
