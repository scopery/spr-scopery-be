package com.company.scopery.modules.externalparty.shared.util;
import com.company.scopery.common.exception.ValidationException;
public final class ExternalPartyEnumParser {
    private ExternalPartyEnumParser(){}
    public static <E extends Enum<E>> E parseRequired(Class<E> type, String raw, String field) {
        if (raw==null||raw.isBlank()) throw new ValidationException(field+" is required");
        try { return Enum.valueOf(type, raw.trim().toUpperCase()); }
        catch (IllegalArgumentException ex) { throw new ValidationException("Invalid "+field+": "+raw); }
    }
}
