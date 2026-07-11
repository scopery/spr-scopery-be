package com.company.scopery.modules.iam.user.domain.valueobject;

public record EmailAddress(String value) {

    public EmailAddress {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Email must not be blank");
        String trimmed = value.trim().toLowerCase();
        if (!trimmed.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
            throw new IllegalArgumentException("Invalid email format: " + trimmed);
        value = trimmed;
    }

    public static EmailAddress of(String value) {
        return new EmailAddress(value);
    }
}
