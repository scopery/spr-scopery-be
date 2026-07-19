package com.company.scopery.modules.documenthub.template.domain.model;

import java.util.UUID;

public record DocumentTemplateVersion(
        UUID id,
        UUID templateId,
        Integer versionNumber,
        String bodyTemplate,
        String outputFormat,
        String status,
        String ast
) {
    public static DocumentTemplateVersion createNative(UUID templateId, Integer versionNumber, String ast) {
        return new DocumentTemplateVersion(UUID.randomUUID(), templateId, versionNumber, null, "NATIVE", "PUBLISHED", ast);
    }
}
