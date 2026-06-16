package com.company.scopery.modules.knowledge.documenttype.domain;

import com.company.scopery.common.exception.ValidationException;

public record DocumentTypeCode(String value) {

    public DocumentTypeCode {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Document type code must not be blank");
        }
        String upper = value.trim().toUpperCase();
        if (!upper.matches("[A-Z][A-Z0-9_]{1,99}")) {
            throw new ValidationException(
                    "Document type code must be 2-100 uppercase letters, digits, or underscores, starting with a letter: " + value);
        }
        value = upper;
    }

    public static DocumentTypeCode of(String value) {
        return new DocumentTypeCode(value);
    }
}
