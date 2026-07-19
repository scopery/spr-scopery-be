package com.company.scopery.modules.documenthub.comment.domain.model;

import java.time.Instant;
import java.util.UUID;

public record DocumentComment(
        UUID id,
        UUID threadId,
        UUID documentId,
        String body,
        Instant deletedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static DocumentComment create(UUID threadId, UUID documentId, String body) {
        Instant now = Instant.now();
        return new DocumentComment(UUID.randomUUID(), threadId, documentId, body, null, now, now);
    }

    public DocumentComment softDelete() {
        return new DocumentComment(id, threadId, documentId, body, Instant.now(), createdAt, Instant.now());
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
