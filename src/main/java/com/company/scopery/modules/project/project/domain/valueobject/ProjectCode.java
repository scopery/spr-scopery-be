package com.company.scopery.modules.project.project.domain.valueobject;

import com.company.scopery.common.exception.ValidationException;

public record ProjectCode(String value) {

    public ProjectCode {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Project code is required");
        }
        if (!value.matches("[A-Z0-9_]{1,100}")) {
            throw new ValidationException("Project code must be uppercase alphanumeric with underscores, max 100 chars");
        }
    }

    public static ProjectCode of(String value) {
        return new ProjectCode(value.trim().toUpperCase());
    }
}
