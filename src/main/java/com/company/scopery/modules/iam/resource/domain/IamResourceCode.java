package com.company.scopery.modules.iam.resource.domain;

public record IamResourceCode(String value) {

    public IamResourceCode {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Resource code must not be blank");
        String trimmed = value.trim().toUpperCase();
        if (trimmed.length() < 2 || trimmed.length() > 100)
            throw new IllegalArgumentException("Resource code must be 2–100 characters");
        if (!trimmed.matches("[A-Z0-9_]+"))
            throw new IllegalArgumentException("Resource code must contain only uppercase letters, digits, or underscores");
        value = trimmed;
    }

    public static IamResourceCode of(String value) {
        return new IamResourceCode(value);
    }
}
