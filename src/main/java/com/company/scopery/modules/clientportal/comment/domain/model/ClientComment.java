package com.company.scopery.modules.clientportal.comment.domain.model;
import com.company.scopery.modules.clientportal.comment.domain.enums.ClientCommentStatus;
import java.time.Instant; import java.util.UUID;
public record ClientComment(UUID id, UUID projectId, String targetType, UUID targetId, String body, UUID authorPortalAccountId, ClientCommentStatus status, int version, Instant createdAt, Instant updatedAt) {
    public static ClientComment create(UUID projectId, String targetType, UUID targetId, String body, UUID authorPortalAccountId) {
        Instant now = Instant.now();
        return new ClientComment(UUID.randomUUID(), projectId, targetType, targetId, body, authorPortalAccountId, ClientCommentStatus.ACTIVE, 0, now, now);
    }
}
