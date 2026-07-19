package com.company.scopery.modules.clientportal.comment.application.response;
import com.company.scopery.modules.clientportal.comment.domain.model.ClientComment;
import java.time.Instant; import java.util.UUID;
public record ClientCommentResponse(UUID id, UUID projectId, String targetType, UUID targetId, String body, UUID authorPortalAccountId, Instant createdAt) {
    public static ClientCommentResponse from(ClientComment e) {
        return new ClientCommentResponse(e.id(), e.projectId(), e.targetType(), e.targetId(), e.body(), e.authorPortalAccountId(), e.createdAt());
    }
}
