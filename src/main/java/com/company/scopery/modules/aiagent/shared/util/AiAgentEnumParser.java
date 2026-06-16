package com.company.scopery.modules.aiagent.shared.util;

import com.company.scopery.common.exception.ValidationException;

public final class AiAgentEnumParser {

    private AiAgentEnumParser() {}

    /**
     * Parses a required enum value from a string. Throws ValidationException if blank or invalid.
     * The errorCode is accepted for documentation purposes; it is included in the exception message
     * since ValidationException does not carry a separate errorCode field in this phase.
     */
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

    /**
     * Parses an optional enum value from a string. Returns null if blank.
     * Throws ValidationException for non-blank invalid values instead of silently ignoring them.
     */
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