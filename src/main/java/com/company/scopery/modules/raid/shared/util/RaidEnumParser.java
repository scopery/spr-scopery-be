package com.company.scopery.modules.raid.shared.util;
import com.company.scopery.modules.raid.shared.error.RaidExceptions;
public final class RaidEnumParser {
    private RaidEnumParser() {}
    public static <E extends Enum<E>> E parseRequired(Class<E> type, String value, String field) {
        if (value == null || value.isBlank()) throw RaidExceptions.invalidStatus(field + " is required");
        try { return Enum.valueOf(type, value.trim().toUpperCase()); }
        catch (IllegalArgumentException ex) { throw RaidExceptions.invalidStatus("Invalid " + field + ": " + value); }
    }
}
