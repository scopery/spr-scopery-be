package com.company.scopery.modules.aiplanning.shared.util;

import com.company.scopery.modules.aiplanning.shared.error.AiPlanningExceptions;

public final class AiPlanningEnumParser {
    private AiPlanningEnumParser() {}

    public static <E extends Enum<E>> E parseRequired(Class<E> type, String value, String field) {
        if (value == null || value.isBlank()) {
            throw AiPlanningExceptions.invalidRunType(field + " is required");
        }
        try {
            return Enum.valueOf(type, value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw AiPlanningExceptions.invalidRunType(value);
        }
    }
}
