package com.company.scopery.modules.iam.role.domain;

public record IamRoleCode(String value) {

    public IamRoleCode {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Role code must not be blank");
        String trimmed = value.trim().toUpperCase();
        if (trimmed.length() < 2 || trimmed.length() > 100)
            throw new IllegalArgumentException("Role code must be 2–100 characters");
        if (!trimmed.matches("[A-Z0-9_]+"))
            throw new IllegalArgumentException("Role code must contain only uppercase letters, digits, or underscores");
        value = trimmed;
    }

    public static IamRoleCode of(String value) {
        return new IamRoleCode(value);
    }
}
