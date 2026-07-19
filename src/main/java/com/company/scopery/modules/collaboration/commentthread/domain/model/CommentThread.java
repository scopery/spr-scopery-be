package com.company.scopery.modules.collaboration.commentthread.domain.model;
import com.company.scopery.modules.collaboration.commentthread.domain.enums.CommentThreadStatus;
import java.time.Instant; import java.util.UUID;
public record CommentThread(
        UUID id, UUID workspaceId, UUID projectId, String targetType, UUID targetId, String title,
        CommentThreadStatus status, boolean internalOnly, boolean clientVisible,
        Instant resolvedAt, UUID resolvedBy, Instant archivedAt, UUID archivedBy, String traceId,
        int version, Instant createdAt, Instant updatedAt
) {
    public static CommentThread create(UUID workspaceId, UUID projectId, String targetType, UUID targetId,
                                       String title, boolean clientVisible) {
        Instant now = Instant.now();
        return new CommentThread(UUID.randomUUID(), workspaceId, projectId, targetType, targetId, title,
                CommentThreadStatus.OPEN, !clientVisible, clientVisible, null, null, null, null, null, 0, now, now);
    }
    public CommentThread resolve(UUID actorId) {
        Instant now = Instant.now();
        return new CommentThread(id, workspaceId, projectId, targetType, targetId, title, CommentThreadStatus.RESOLVED,
                internalOnly, clientVisible, now, actorId, archivedAt, archivedBy, traceId, version, createdAt, now);
    }
    public CommentThread archive(UUID actorId) {
        Instant now = Instant.now();
        return new CommentThread(id, workspaceId, projectId, targetType, targetId, title, CommentThreadStatus.ARCHIVED,
                internalOnly, clientVisible, resolvedAt, resolvedBy, now, actorId, traceId, version, createdAt, now);
    }
}
