package com.company.scopery.modules.documenthub.comment.domain.model;

import java.time.Instant;
import java.util.UUID;

public record DocumentCommentReply(
        UUID id,
        UUID commentId,
        UUID threadId,
        String body,
        Instant deletedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static DocumentCommentReply create(UUID commentId, UUID threadId, String body) {
        Instant now = Instant.now();
        return new DocumentCommentReply(UUID.randomUUID(), commentId, threadId, body, null, now, now);
    }

    public DocumentCommentReply softDelete() {
        return new DocumentCommentReply(id, commentId, threadId, body, Instant.now(), createdAt, Instant.now());
    }
}
