package com.company.scopery.modules.eventregistry.shared.util;

import com.company.scopery.common.exception.ValidationException;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.VariableType;
import com.company.scopery.modules.eventregistry.shared.error.EventRegistryErrorCatalog;

public final class EventRegistryEnumParser {

    private EventRegistryEnumParser() {}

    public static VariableType parseVariableType(String value) {
        return parseRequired(VariableType.class, value,
                EventRegistryErrorCatalog.INVALID_EVENT_VARIABLE_TYPE.code(), "variableType");
    }

    public static <E extends Enum<E>> E parseRequired(Class<E> enumType, String value,
                                                       String errorCode, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " is required [" + errorCode + "]");
        }
        try {
            return Enum.valueOf(enumType, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    "Invalid " + fieldName + ": '" + value.trim() + "' [" + errorCode + "]");
        }
    }

    public static <E extends Enum<E>> E parseOptional(Class<E> enumType, String value,
                                                       String errorCode, String fieldName) {
        if (value == null || value.isBlank()) return null;
        try {
            return Enum.valueOf(enumType, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    "Invalid " + fieldName + ": '" + value.trim() + "' [" + errorCode + "]");
        }
    }
}
