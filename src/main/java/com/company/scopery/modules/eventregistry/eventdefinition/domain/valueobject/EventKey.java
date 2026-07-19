package com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Machine-friendly event key.
 * Accepts legacy uppercase snake ({@code USER_CREATED}) and Phase 05 lowercase dot style
 * ({@code user.created}).
 */
public final class EventKey {

    private static final Pattern UPPER_SNAKE = Pattern.compile("^[A-Z][A-Z0-9_]{1,149}$");
    private static final Pattern LOWER_DOT = Pattern.compile("^[a-z][a-z0-9_.]{1,149}$");

    private final String value;

    private EventKey(String value) {
        this.value = value;
    }

    public static EventKey of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Event key must not be blank");
        }
        String trimmed = raw.trim();
        // Prefer lowercase-dot style only when a dot is present (e.g. task.assigned).
        if (trimmed.contains(".") && LOWER_DOT.matcher(trimmed).matches()) {
            return new EventKey(trimmed);
        }
        String upper = trimmed.toUpperCase();
        if (UPPER_SNAKE.matcher(upper).matches()) {
            return new EventKey(upper);
        }
        throw new IllegalArgumentException(
                "Event key must be uppercase snake (USER_CREATED) or lowercase dot style (user.created): "
                        + trimmed);
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
