package com.company.scopery.modules.project.template.domain.valueobject;

import com.company.scopery.common.exception.ValidationException;

public record TemplateCode(String value) {

    public TemplateCode {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Template code is required");
        }
        if (!value.matches("[A-Z0-9_]{1,100}")) {
            throw new ValidationException(
                    "Template code must be uppercase alphanumeric with underscores, max 100 chars");
        }
    }

    public static TemplateCode of(String value) {
        return new TemplateCode(value.trim().toUpperCase());
    }
}
