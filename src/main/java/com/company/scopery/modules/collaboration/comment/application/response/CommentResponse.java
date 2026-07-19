package com.company.scopery.modules.collaboration.comment.application.response;
import com.company.scopery.modules.collaboration.comment.domain.model.Comment;
import java.time.Instant; import java.util.UUID;
public record CommentResponse(UUID id, UUID threadId, UUID parentCommentId, String authorType, UUID authorId, String body, String status, Instant createdAt) {
    public static CommentResponse from(Comment c) { return new CommentResponse(c.id(), c.threadId(), c.parentCommentId(), c.authorType().name(), c.authorId(), c.body(), c.status().name(), c.createdAt()); }
}
