package com.company.scopery.modules.iam.permission.domain.valueobject;

public record IamPermissionCode(String value) {

    public IamPermissionCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Permission code must not be blank");
        }
        String trimmed = value.trim().toUpperCase();
        if (trimmed.length() < 2 || trimmed.length() > 100) {
            throw new IllegalArgumentException("Permission code must be 2-100 characters");
        }
        if (!trimmed.matches("[A-Z0-9_]+")) {
            throw new IllegalArgumentException("Permission code must contain only uppercase letters, digits, or underscores");
        }
        value = trimmed;
    }

    public static IamPermissionCode of(String value) {
        return new IamPermissionCode(value);
    }
}
