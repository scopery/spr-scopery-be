package com.company.scopery.modules.iam.user.domain.valueobject;

public record Username(String value) {

    public Username {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Username must not be blank");
        String trimmed = value.trim().toLowerCase();
        if (trimmed.length() < 3 || trimmed.length() > 100)
            throw new IllegalArgumentException("Username must be 3–100 characters");
        if (!trimmed.matches("[a-z0-9._-]+"))
            throw new IllegalArgumentException("Username must contain only letters, digits, '.', '_', or '-'");
        value = trimmed;
    }

    public static Username of(String value) {
        return new Username(value);
    }
}
