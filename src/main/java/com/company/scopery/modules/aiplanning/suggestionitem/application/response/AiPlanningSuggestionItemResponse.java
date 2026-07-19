package com.company.scopery.modules.aiplanning.suggestionitem.application.response;

import com.company.scopery.modules.aiplanning.suggestionitem.domain.model.AiPlanningSuggestionItem;

import java.time.Instant;
import java.util.UUID;

public record AiPlanningSuggestionItemResponse(
        UUID id, UUID suggestionId, UUID projectId, String itemType, String targetType, UUID targetId,
        String operation, String title, String description, String proposedPayloadJson, String rationale,
        String confidenceLabel, String status, String applyAction, String applyResultJson,
        Instant reviewedAt, UUID reviewedBy, Instant appliedAt, UUID appliedBy, int version
) {
    public static AiPlanningSuggestionItemResponse from(AiPlanningSuggestionItem i) {
        return new AiPlanningSuggestionItemResponse(
                i.id(), i.suggestionId(), i.projectId(), i.itemType().name(), i.targetType(), i.targetId(),
                i.operation().name(), i.title(), i.description(), i.proposedPayloadJson(), i.rationale(),
                i.confidenceLabel(), i.status().name(), i.applyAction(), i.applyResultJson(),
                i.reviewedAt(), i.reviewedBy(), i.appliedAt(), i.appliedBy(), i.version());
    }
}
