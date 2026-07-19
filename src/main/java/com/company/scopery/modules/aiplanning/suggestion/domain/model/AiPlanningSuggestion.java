package com.company.scopery.modules.aiplanning.suggestion.domain.model;

import com.company.scopery.modules.aiplanning.suggestion.domain.enums.SuggestionStatus;
import com.company.scopery.modules.aiplanning.suggestion.domain.enums.SuggestionType;

import java.time.Instant;
import java.util.UUID;

public record AiPlanningSuggestion(
        UUID id,
        UUID planningRunId,
        UUID projectId,
        UUID workspaceId,
        SuggestionType suggestionType,
        String title,
        String summary,
        String rationale,
        String confidenceLabel,
        SuggestionStatus status,
        String sourceReferencesJson,
        Instant reviewedAt,
        UUID reviewedBy,
        Instant appliedAt,
        UUID appliedBy,
        Instant rejectedAt,
        UUID rejectedBy,
        String rejectionReason,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static AiPlanningSuggestion create(
            UUID planningRunId, UUID projectId, UUID workspaceId, SuggestionType type,
            String title, String summary, String rationale, String confidenceLabel, String sourceReferencesJson) {
        Instant now = Instant.now();
        return new AiPlanningSuggestion(
                UUID.randomUUID(), planningRunId, projectId, workspaceId, type, title, summary, rationale,
                confidenceLabel, SuggestionStatus.GENERATED, sourceReferencesJson,
                null, null, null, null, null, null, null, 0, now, now);
    }

    public AiPlanningSuggestion startReview(UUID actorId) {
        requireStatus(SuggestionStatus.GENERATED, SuggestionStatus.UNDER_REVIEW);
        return withReview(SuggestionStatus.UNDER_REVIEW, actorId, null, null, null, null, null);
    }

    public AiPlanningSuggestion accept(UUID actorId) {
        requireStatus(SuggestionStatus.GENERATED, SuggestionStatus.UNDER_REVIEW, SuggestionStatus.PARTIALLY_ACCEPTED);
        return withReview(SuggestionStatus.ACCEPTED, actorId, Instant.now(), null, null, null, null);
    }

    public AiPlanningSuggestion reject(UUID actorId, String reason) {
        requireStatus(SuggestionStatus.GENERATED, SuggestionStatus.UNDER_REVIEW, SuggestionStatus.PARTIALLY_ACCEPTED, SuggestionStatus.ACCEPTED);
        return withReview(SuggestionStatus.REJECTED, actorId, Instant.now(), null, Instant.now(), actorId, reason);
    }

    public AiPlanningSuggestion archive(UUID actorId) {
        return withReview(SuggestionStatus.ARCHIVED, actorId, reviewedAt, appliedAt, rejectedAt, rejectedBy, rejectionReason);
    }

    public AiPlanningSuggestion markApplied(UUID actorId) {
        requireStatus(SuggestionStatus.ACCEPTED, SuggestionStatus.PARTIALLY_ACCEPTED);
        return new AiPlanningSuggestion(
                id, planningRunId, projectId, workspaceId, suggestionType, title, summary, rationale, confidenceLabel,
                SuggestionStatus.APPLIED, sourceReferencesJson, reviewedAt, reviewedBy,
                Instant.now(), actorId, rejectedAt, rejectedBy, rejectionReason, version, createdAt, Instant.now());
    }

    public AiPlanningSuggestion markPartiallyApplied(UUID actorId) {
        return new AiPlanningSuggestion(
                id, planningRunId, projectId, workspaceId, suggestionType, title, summary, rationale, confidenceLabel,
                SuggestionStatus.PARTIALLY_APPLIED, sourceReferencesJson, reviewedAt, reviewedBy,
                Instant.now(), actorId, rejectedAt, rejectedBy, rejectionReason, version, createdAt, Instant.now());
    }

    private AiPlanningSuggestion withReview(SuggestionStatus status, UUID reviewedBy, Instant reviewedAt,
                                            Instant appliedAt, Instant rejectedAt, UUID rejectedBy, String rejectionReason) {
        return new AiPlanningSuggestion(
                id, planningRunId, projectId, workspaceId, suggestionType, title, summary, rationale, confidenceLabel,
                status, sourceReferencesJson,
                reviewedAt != null ? reviewedAt : Instant.now(), reviewedBy,
                appliedAt, appliedBy, rejectedAt, rejectedBy, rejectionReason, version, createdAt, Instant.now());
    }

    private void requireStatus(SuggestionStatus... allowed) {
        for (SuggestionStatus s : allowed) {
            if (status == s) return;
        }
        throw new IllegalStateException("Invalid suggestion status: " + status);
    }
}
