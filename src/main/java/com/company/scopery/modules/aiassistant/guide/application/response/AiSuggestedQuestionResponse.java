package com.company.scopery.modules.aiassistant.guide.application.response;

import com.company.scopery.modules.aiassistant.domain.model.AiSuggestedQuestion;

import java.util.UUID;

public record AiSuggestedQuestionResponse(
        UUID id,
        String code,
        String pageCode,
        String questionText,
        int displayOrder
) {
    public static AiSuggestedQuestionResponse from(AiSuggestedQuestion q) {
        return new AiSuggestedQuestionResponse(
                q.id(),
                q.code(),
                q.pageCode(),
                q.questionText(),
                q.displayOrder()
        );
    }
}
