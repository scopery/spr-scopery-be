package com.company.scopery.modules.aiagent.deployment.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

public final class ModelDeploymentCode {

    private static final Pattern VALID_PATTERN = Pattern.compile("^[A-Z0-9_]+$");

    private final String value;

    private ModelDeploymentCode(String value) {
        this.value = value;
    }

    public static ModelDeploymentCode of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Deployment code must not be blank");
        }
        String normalized = raw.strip().toUpperCase();
        if (!VALID_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "Deployment code must contain only uppercase letters, numbers, and underscores: " + normalized);
        }
        return new ModelDeploymentCode(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModelDeploymentCode that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
