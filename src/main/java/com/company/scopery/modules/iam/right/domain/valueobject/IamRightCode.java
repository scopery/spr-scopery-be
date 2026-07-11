package com.company.scopery.modules.iam.right.domain.valueobject;

public record IamRightCode(String value) {

    public IamRightCode {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Right code must not be blank");
        String trimmed = value.trim().toUpperCase();
        if (trimmed.length() < 2 || trimmed.length() > 100)
            throw new IllegalArgumentException("Right code must be 2–100 characters");
        if (!trimmed.matches("[A-Z0-9_]+"))
            throw new IllegalArgumentException("Right code must contain only uppercase letters, digits, or underscores");
        value = trimmed;
    }

    public static IamRightCode of(String value) {
        return new IamRightCode(value);
    }
}
