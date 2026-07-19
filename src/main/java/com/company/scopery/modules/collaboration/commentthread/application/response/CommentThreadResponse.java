package com.company.scopery.modules.collaboration.commentthread.application.response;
import com.company.scopery.modules.collaboration.commentthread.domain.model.CommentThread;
import java.time.Instant; import java.util.UUID;
public record CommentThreadResponse(UUID id, String targetType, UUID targetId, String title, String status, boolean internalOnly, boolean clientVisible, Instant resolvedAt) {
    public static CommentThreadResponse from(CommentThread t) { return new CommentThreadResponse(t.id(), t.targetType(), t.targetId(), t.title(), t.status().name(), t.internalOnly(), t.clientVisible(), t.resolvedAt()); }
}
