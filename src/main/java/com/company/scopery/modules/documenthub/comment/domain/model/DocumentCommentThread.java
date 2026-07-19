package com.company.scopery.modules.documenthub.comment.domain.model;

import com.company.scopery.modules.documenthub.comment.domain.enums.CommentThreadStatus;

import java.time.Instant;
import java.util.UUID;

public record DocumentCommentThread(
        UUID id,
        UUID documentId,
        UUID workspaceId,
        UUID projectId,
        String blockId,
        String anchorText,
        CommentThreadStatus status,
        UUID resolvedBy,
        Instant resolvedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static DocumentCommentThread create(UUID documentId, UUID workspaceId, UUID projectId,
                                                String blockId, String anchorText) {
        Instant now = Instant.now();
        return new DocumentCommentThread(UUID.randomUUID(), documentId, workspaceId, projectId,
                blockId, anchorText, CommentThreadStatus.OPEN, null, null, now, now);
    }

    public DocumentCommentThread resolve(UUID actorId) {
        return new DocumentCommentThread(id, documentId, workspaceId, projectId, blockId, anchorText,
                CommentThreadStatus.RESOLVED, actorId, Instant.now(), createdAt, Instant.now());
    }
}
