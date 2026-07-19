package com.company.scopery.modules.aiagent.tool.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Tool registry codes use camelCase or dot-namespaced form (e.g. knowledge.search).
 * Allowed: letters, digits, underscores, dots. Must start with a letter.
 */
public final class AiToolCode {

    private static final Pattern VALID_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_.]{1,99}$");

    private final String value;

    private AiToolCode(String value) {
        this.value = value;
    }

    public static AiToolCode of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("AI tool code must not be blank");
        }
        String normalized = raw.trim();
        if (!VALID_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "AI tool code must start with a letter and contain only letters, digits, underscores, dots: " + normalized);
        }
        return new AiToolCode(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AiToolCode that)) return false;
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
