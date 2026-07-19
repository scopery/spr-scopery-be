package com.company.scopery.modules.aiassistant.guide.application.response;

import com.company.scopery.modules.aiassistant.domain.model.AiGuideDefinition;

import java.util.UUID;

public record AiGuideResponse(
        UUID id,
        String code,
        String pageCode,
        String fieldCode,
        String actionCode,
        String locale,
        String title,
        String bodyMarkdown
) {
    public static AiGuideResponse from(AiGuideDefinition g) {
        return new AiGuideResponse(
                g.id(),
                g.code(),
                g.pageCode(),
                g.fieldCode(),
                g.actionCode(),
                g.locale(),
                g.title(),
                g.bodyMarkdown()
        );
    }
}
