package com.company.scopery.modules.documenthub.comment.application.response;

import com.company.scopery.modules.documenthub.comment.domain.model.DocumentCommentThread;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CommentThreadResponse(
        UUID id,
        UUID documentId,
        String blockId,
        String anchorText,
        String status,
        UUID resolvedBy,
        Instant resolvedAt,
        List<CommentResponse> comments,
        Instant createdAt
) {
    public static CommentThreadResponse from(DocumentCommentThread t, List<CommentResponse> comments) {
        return new CommentThreadResponse(t.id(), t.documentId(), t.blockId(), t.anchorText(),
                t.status().name(), t.resolvedBy(), t.resolvedAt(), comments, t.createdAt());
    }

    public static CommentThreadResponse from(DocumentCommentThread t) {
        return from(t, List.of());
    }
}
