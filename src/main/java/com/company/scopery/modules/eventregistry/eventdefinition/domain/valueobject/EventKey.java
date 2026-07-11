package com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

public final class EventKey {

    private static final Pattern VALID_PATTERN = Pattern.compile("^[A-Z0-9_]+$");

    private final String value;

    private EventKey(String value) {
        this.value = value;
    }

    public static EventKey of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Event key must not be blank");
        }
        String normalized = raw.trim().toUpperCase();
        if (!VALID_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "Event key must contain only uppercase letters, numbers, and underscores: " + normalized);
        }
        return new EventKey(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventKey that)) return false;
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
