package com.company.scopery.modules.iam.shared.util;

import com.company.scopery.common.exception.ValidationException;

public final class IamEnumParser {

    private IamEnumParser() {}

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
