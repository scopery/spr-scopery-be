package com.company.scopery.modules.documenthub.shared.util;
import com.company.scopery.common.exception.ValidationException;
public final class DocumentHubEnumParser {
    private DocumentHubEnumParser() {}
    public static <E extends Enum<E>> E parseRequired(Class<E> type, String raw, String field) {
        if (raw == null || raw.isBlank()) throw new ValidationException(field + " is required");
        try { return Enum.valueOf(type, raw.trim().toUpperCase()); }
        catch (IllegalArgumentException ex) { throw new ValidationException("Invalid " + field + ": " + raw); }
    }
}
