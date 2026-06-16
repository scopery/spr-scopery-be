package com.company.scopery.modules.aiagent.aimodel.domain;

import java.util.Objects;
import java.util.regex.Pattern;

public final class AiModelCode {

    private static final Pattern VALID_PATTERN = Pattern.compile("^[A-Z0-9_]+$");

    private final String value;

    private AiModelCode(String value) {
        this.value = value;
    }

    public static AiModelCode of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("AI model code must not be blank");
        }
        String normalized = raw.strip().toUpperCase();
        if (!VALID_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "AI model code must contain only uppercase letters, numbers, and underscores: " + normalized);
        }
        return new AiModelCode(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AiModelCode that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}