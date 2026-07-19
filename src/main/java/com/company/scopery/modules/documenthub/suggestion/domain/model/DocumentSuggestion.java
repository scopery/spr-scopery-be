package com.company.scopery.modules.documenthub.suggestion.domain.model;

import com.company.scopery.modules.documenthub.suggestion.domain.enums.SuggestionStatus;

import java.time.Instant;
import java.util.UUID;

public record DocumentSuggestion(
        UUID id,
        UUID documentId,
        UUID workspaceId,
        UUID projectId,
        long targetRevisionNo,
        String description,
        SuggestionStatus status,
        UUID acceptedBy,
        Instant acceptedAt,
        Long acceptedRevisionNo,
        UUID rejectedBy,
        Instant rejectedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static DocumentSuggestion create(UUID documentId, UUID workspaceId, UUID projectId,
                                             long targetRevisionNo, String description) {
        Instant now = Instant.now();
        return new DocumentSuggestion(UUID.randomUUID(), documentId, workspaceId, projectId,
                targetRevisionNo, description, SuggestionStatus.PENDING,
                null, null, null, null, null, now, now);
    }

    public DocumentSuggestion accept(UUID actorId, long resultRevisionNo) {
        return new DocumentSuggestion(id, documentId, workspaceId, projectId, targetRevisionNo, description,
                SuggestionStatus.ACCEPTED, actorId, Instant.now(), resultRevisionNo, null, null, createdAt, Instant.now());
    }

    public DocumentSuggestion reject(UUID actorId) {
        return new DocumentSuggestion(id, documentId, workspaceId, projectId, targetRevisionNo, description,
                SuggestionStatus.REJECTED, null, null, null, actorId, Instant.now(), createdAt, Instant.now());
    }
}
