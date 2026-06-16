package com.company.scopery.modules.workspace.workspace.domain;

import com.company.scopery.common.exception.ValidationException;

public record WorkspaceCode(String value) {

    public WorkspaceCode {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Workspace code must not be blank");
        }
        if (!value.matches("[A-Z0-9_]+")) {
            throw new ValidationException(
                    "Workspace code must contain only uppercase letters, digits, and underscores: '" + value + "'");
        }
    }

    public static WorkspaceCode of(String raw) {
        if (raw == null) {
            throw new ValidationException("Workspace code must not be blank");
        }
        String normalized = raw.trim().toUpperCase();
        return new WorkspaceCode(normalized);
    }
}
