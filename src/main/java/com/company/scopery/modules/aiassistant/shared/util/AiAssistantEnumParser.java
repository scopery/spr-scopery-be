package com.company.scopery.modules.aiassistant.shared.util;

import com.company.scopery.common.exception.ValidationException;

public final class AiAssistantEnumParser {

    private AiAssistantEnumParser() {}

    /**
     * Parses a required enum value from a string. Throws ValidationException if blank or invalid.
     */
    public static <E extends Enum<E>> E parseRequired(Class<E> enumType, String value,
                                                       String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " is required");
        }
        try {
            return Enum.valueOf(enumType, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    "Invalid " + fieldName + ": '" + value.trim() + "'");
        }
    }

    /**
     * Parses an optional enum value from a string. Returns null if blank.
     * Throws ValidationException for non-blank invalid values.
     */
    public static <E extends Enum<E>> E parseOptional(Class<E> enumType, String value,
                                                       String fieldName) {
        if (value == null || value.isBlank()) return null;
        try {
            return Enum.valueOf(enumType, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    "Invalid " + fieldName + ": '" + value.trim() + "'");
        }
    }
}
