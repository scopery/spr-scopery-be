package com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

public final class EventDefinitionCode {

    /** Phase 05: uppercase snake, starts with letter, length 3–150. */
    private static final Pattern VALID_PATTERN = Pattern.compile("^[A-Z][A-Z0-9_]{2,149}$");

    private final String value;

    private EventDefinitionCode(String value) {
        this.value = value;
    }

    public static EventDefinitionCode of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Event definition code must not be blank");
        }
        String normalized = raw.trim().toUpperCase();
        if (!VALID_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "Event definition code must match ^[A-Z][A-Z0-9_]{2,149}$: " + normalized);
        }
        return new EventDefinitionCode(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventDefinitionCode that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
