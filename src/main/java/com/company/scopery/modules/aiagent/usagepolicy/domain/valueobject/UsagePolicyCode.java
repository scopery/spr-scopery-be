package com.company.scopery.modules.aiagent.usagepolicy.domain.valueobject;

import java.util.regex.Pattern;

public final class UsagePolicyCode {

    private static final Pattern VALID_PATTERN = Pattern.compile("^[A-Z0-9_]+$");

    private final String value;

    private UsagePolicyCode(String value) {
        this.value = value;
    }

    public static UsagePolicyCode of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Usage policy code must not be blank");
        }
        String normalized = raw.trim().toUpperCase();
        if (!VALID_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "Usage policy code must contain only uppercase letters, numbers, and underscores: " + normalized);
        }
        return new UsagePolicyCode(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsagePolicyCode)) return false;
        return value.equals(((UsagePolicyCode) o).value);
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
