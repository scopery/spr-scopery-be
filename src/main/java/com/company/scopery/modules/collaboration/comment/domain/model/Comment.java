package com.company.scopery.modules.collaboration.comment.domain.model;
import com.company.scopery.modules.collaboration.comment.domain.enums.CommentAuthorType;
import com.company.scopery.modules.collaboration.comment.domain.enums.CommentStatus;
import java.time.Instant; import java.util.UUID;
public record Comment(
        UUID id, UUID workspaceId, UUID projectId, UUID threadId, UUID parentCommentId,
        CommentAuthorType authorType, UUID authorId, String authorDisplayNameSnapshot, String body,
        CommentStatus status, boolean internalOnly, boolean clientVisible,
        Instant editedAt, UUID editedBy, Instant deletedAt, UUID deletedBy, String traceId,
        int version, Instant createdAt, Instant updatedAt
) {
    public static Comment create(UUID workspaceId, UUID projectId, UUID threadId, UUID parentId,
                                 CommentAuthorType authorType, UUID authorId, String displayName, String body, boolean clientVisible) {
        Instant now = Instant.now();
        return new Comment(UUID.randomUUID(), workspaceId, projectId, threadId, parentId, authorType, authorId,
                displayName, body, CommentStatus.ACTIVE, !clientVisible, clientVisible, null, null, null, null, null, 0, now, now);
    }
    public Comment update(String body, UUID actorId) {
        Instant now = Instant.now();
        return new Comment(id, workspaceId, projectId, threadId, parentCommentId, authorType, authorId,
                authorDisplayNameSnapshot, body, CommentStatus.EDITED, internalOnly, clientVisible,
                now, actorId, deletedAt, deletedBy, traceId, version, createdAt, now);
    }
    public Comment softDelete(UUID actorId) {
        Instant now = Instant.now();
        return new Comment(id, workspaceId, projectId, threadId, parentCommentId, authorType, authorId,
                authorDisplayNameSnapshot, body, CommentStatus.DELETED_SOFT, internalOnly, clientVisible,
                editedAt, editedBy, now, actorId, traceId, version, createdAt, now);
    }
}
