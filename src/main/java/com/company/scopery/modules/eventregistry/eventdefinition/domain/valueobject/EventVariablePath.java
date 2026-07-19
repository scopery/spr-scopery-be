package com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Dot-notation path for event payload variables (e.g. actor.userId, invitation.acceptUrl).
 */
public final class EventVariablePath {

    private static final Pattern VALID_PATTERN =
            Pattern.compile("^[A-Za-z][A-Za-z0-9_]*(\\.[A-Za-z][A-Za-z0-9_]*)*$");

    private final String value;

    private EventVariablePath(String value) {
        this.value = value;
    }

    public static EventVariablePath of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Event variable path must not be blank");
        }
        String normalized = raw.trim();
        if (normalized.startsWith(".") || normalized.endsWith(".")) {
            throw new IllegalArgumentException(
                    "Event variable path must not start or end with a dot: " + normalized);
        }
        if (!VALID_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "Event variable path must use dot notation with alphanumeric segments: " + normalized);
        }
        return new EventVariablePath(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventVariablePath that)) return false;
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
