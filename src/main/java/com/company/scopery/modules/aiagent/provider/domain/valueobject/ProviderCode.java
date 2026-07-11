package com.company.scopery.modules.aiagent.provider.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

public final class ProviderCode {

    private static final Pattern VALID_PATTERN = Pattern.compile("^[A-Z0-9_]+$");

    private final String value;

    private ProviderCode(String value) {
        this.value = value;
    }

    public static ProviderCode of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Provider code must not be blank");
        }
        String normalized = raw.trim().toUpperCase();
        if (!VALID_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                "Provider code must contain only uppercase letters, numbers, and underscores: " + normalized
            );
        }
        return new ProviderCode(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProviderCode that)) return false;
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
