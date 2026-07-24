package com.company.scopery.modules.aiassistant.guide.application.response;

import com.company.scopery.modules.aiassistant.domain.model.AiGuideDefinition;

public record AdminGuideDefinitionResponse(
        String id,
        String code,
        String pageCode,
        String fieldCode,
        String actionCode,
        String locale,
        String title,
        String bodyMarkdown,
        int metadataVersion,
        String sourceKind,
        String status,
        String createdAt,
        String updatedAt
) {
    public static AdminGuideDefinitionResponse from(AiGuideDefinition domain) {
        return new AdminGuideDefinitionResponse(
                domain.id() != null ? domain.id().toString() : null,
                domain.code(),
                domain.pageCode(),
                domain.fieldCode(),
                domain.actionCode(),
                domain.locale(),
                domain.title(),
                domain.bodyMarkdown(),
                domain.metadataVersion(),
                domain.sourceKind(),
                domain.status(),
                domain.createdAt() != null ? domain.createdAt().toString() : null,
                domain.updatedAt() != null ? domain.updatedAt().toString() : null
        );
    }
}
