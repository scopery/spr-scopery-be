package com.company.scopery.modules.aiassistant.message.application.response;

import com.company.scopery.modules.aiassistant.domain.model.AiMessageCitation;

import java.util.UUID;

public record AiCitationResponse(
        UUID id,
        int ordinal,
        String sourceType,
        UUID sourceRefId,
        String title,
        String quotedFragment,
        String accessValidationResult
) {
    public static AiCitationResponse from(AiMessageCitation c) {
        return new AiCitationResponse(
                c.id(),
                c.ordinal(),
                c.sourceType(),
                c.sourceRefId(),
                c.title(),
                c.quotedFragment(),
                c.accessValidationResult()
        );
    }
}
