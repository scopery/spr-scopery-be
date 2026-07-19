package com.company.scopery.modules.documenthub.comment.application.response;

import com.company.scopery.modules.documenthub.comment.domain.model.DocumentComment;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID threadId,
        String body,
        boolean deleted,
        String createdBy,
        Instant createdAt
) {
    public static CommentResponse from(DocumentComment c) {
        return new CommentResponse(c.id(), c.threadId(),
                c.isDeleted() ? null : c.body(), c.isDeleted(),
                null, c.createdAt());
    }
}
