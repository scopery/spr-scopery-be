package com.company.scopery.modules.workspace.team.domain.valueobject;

import com.company.scopery.common.exception.ValidationException;

public record TeamCode(String value) {

    public TeamCode {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Team code must not be blank");
        }
        if (!value.matches("[A-Z0-9_]+")) {
            throw new ValidationException(
                    "Team code must contain only uppercase letters, digits, and underscores: '" + value + "'");
        }
    }

    public static TeamCode of(String raw) {
        if (raw == null) {
            throw new ValidationException("Team code must not be blank");
        }
        String normalized = raw.trim().toUpperCase();
        return new TeamCode(normalized);
    }
}
