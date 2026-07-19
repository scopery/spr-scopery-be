package com.company.scopery.modules.documenthub.template.domain.model;

import java.time.Instant;
import java.util.UUID;

public record DocumentTemplateVariable(
        UUID id,
        UUID templateVersionId,
        String variableKey,
        String label,
        String variableType,
        boolean required,
        String defaultValue,
        boolean sensitive,
        int ordinal,
        Instant createdAt,
        Instant updatedAt
) {
    public static DocumentTemplateVariable create(UUID templateVersionId, String variableKey, String label,
                                                   String variableType, boolean required, String defaultValue,
                                                   boolean sensitive, int ordinal) {
        Instant now = Instant.now();
        return new DocumentTemplateVariable(UUID.randomUUID(), templateVersionId, variableKey, label,
                variableType, required, defaultValue, sensitive, ordinal, now, now);
    }
}
