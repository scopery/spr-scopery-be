package com.company.scopery.modules.aiagent.eventconfig.domain.valueobject;

import java.util.regex.Pattern;

public final class EventConfigCode {

    private static final Pattern VALID_PATTERN = Pattern.compile("^[A-Z0-9_]+$");

    private final String value;

    private EventConfigCode(String value) {
        this.value = value;
    }

    public static EventConfigCode of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Event config code must not be blank");
        }
        String normalized = raw.trim().toUpperCase();
        if (!VALID_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "Event config code must contain only uppercase letters, numbers, and underscores: " + normalized);
        }
        return new EventConfigCode(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventConfigCode)) return false;
        return value.equals(((EventConfigCode) o).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
