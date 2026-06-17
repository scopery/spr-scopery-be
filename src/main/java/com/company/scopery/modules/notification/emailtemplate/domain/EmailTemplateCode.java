package com.company.scopery.modules.notification.emailtemplate.domain;

import java.util.regex.Pattern;

public record EmailTemplateCode(String value) {

    private static final Pattern VALID = Pattern.compile("^[A-Z][A-Z0-9_]{1,99}$");

    public EmailTemplateCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email template code must not be blank");
        }
        if (!VALID.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "Email template code must be uppercase alphanumeric with underscores (2-100 chars): " + value);
        }
    }

    public static EmailTemplateCode of(String value) {
        return new EmailTemplateCode(value.trim().toUpperCase());
    }

    @Override
    public String toString() {
        return value;
    }
}
