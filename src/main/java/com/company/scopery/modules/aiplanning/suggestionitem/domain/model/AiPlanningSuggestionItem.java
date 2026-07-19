package com.company.scopery.modules.aiplanning.suggestionitem.domain.model;

import com.company.scopery.modules.aiplanning.suggestionitem.domain.enums.SuggestionItemOperation;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.enums.SuggestionItemStatus;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.enums.SuggestionItemType;

import java.time.Instant;
import java.util.UUID;

public record AiPlanningSuggestionItem(
        UUID id,
        UUID suggestionId,
        UUID projectId,
        SuggestionItemType itemType,
        String targetType,
        UUID targetId,
        SuggestionItemOperation operation,
        String title,
        String description,
        String proposedPayloadJson,
        String rationale,
        String confidenceLabel,
        SuggestionItemStatus status,
        String applyAction,
        String applyResultJson,
        Instant reviewedAt,
        UUID reviewedBy,
        Instant appliedAt,
        UUID appliedBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static AiPlanningSuggestionItem create(
            UUID suggestionId, UUID projectId, SuggestionItemType itemType, SuggestionItemOperation operation,
            String title, String description, String proposedPayloadJson, String rationale, String confidenceLabel,
            String targetType, UUID targetId) {
        Instant now = Instant.now();
        return new AiPlanningSuggestionItem(
                UUID.randomUUID(), suggestionId, projectId, itemType, targetType, targetId, operation,
                title, description, proposedPayloadJson, rationale, confidenceLabel, SuggestionItemStatus.PROPOSED,
                null, null, null, null, null, null, 0, now, now);
    }

    public AiPlanningSuggestionItem accept(UUID actorId) {
        require(SuggestionItemStatus.PROPOSED);
        return withStatus(SuggestionItemStatus.ACCEPTED, actorId, Instant.now(), null, null, applyAction, applyResultJson);
    }

    public AiPlanningSuggestionItem reject(UUID actorId) {
        require(SuggestionItemStatus.PROPOSED, SuggestionItemStatus.ACCEPTED);
        return withStatus(SuggestionItemStatus.REJECTED, actorId, Instant.now(), null, null, applyAction, applyResultJson);
    }

    public AiPlanningSuggestionItem markApplied(UUID actorId, String applyAction, String applyResultJson) {
        require(SuggestionItemStatus.ACCEPTED);
        return withStatus(SuggestionItemStatus.APPLIED, reviewedBy, reviewedAt, Instant.now(), actorId, applyAction, applyResultJson);
    }

    public AiPlanningSuggestionItem markFailed(String applyAction, String applyResultJson) {
        return withStatus(SuggestionItemStatus.FAILED, reviewedBy, reviewedAt, Instant.now(), appliedBy, applyAction, applyResultJson);
    }

    public AiPlanningSuggestionItem markSkipped(String reasonJson) {
        return withStatus(SuggestionItemStatus.SKIPPED, reviewedBy, reviewedAt, Instant.now(), appliedBy, applyAction, reasonJson);
    }

    private AiPlanningSuggestionItem withStatus(SuggestionItemStatus status, UUID reviewedBy, Instant reviewedAt,
                                                Instant appliedAt, UUID appliedBy, String applyAction, String applyResultJson) {
        return new AiPlanningSuggestionItem(
                id, suggestionId, projectId, itemType, targetType, targetId, operation, title, description,
                proposedPayloadJson, rationale, confidenceLabel, status, applyAction, applyResultJson,
                reviewedAt, reviewedBy, appliedAt, appliedBy, version, createdAt, Instant.now());
    }

    private void require(SuggestionItemStatus... allowed) {
        for (SuggestionItemStatus s : allowed) {
            if (status == s) return;
        }
        throw new IllegalStateException("Invalid item status: " + status);
    }
}
