package com.company.scopery.modules.workspace.organization.domain;

import com.company.scopery.common.exception.ValidationException;

public record OrganizationCode(String value) {

    public OrganizationCode {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Organization code must not be blank");
        }
        if (!value.matches("[A-Z0-9_]+")) {
            throw new ValidationException(
                    "Organization code must contain only uppercase letters, digits, and underscores: '" + value + "'");
        }
    }

    public static OrganizationCode of(String raw) {
        if (raw == null) {
            throw new ValidationException("Organization code must not be blank");
        }
        String normalized = raw.trim().toUpperCase();
        return new OrganizationCode(normalized);
    }
}
