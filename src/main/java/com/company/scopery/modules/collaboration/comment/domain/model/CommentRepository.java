package com.company.scopery.modules.collaboration.comment.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface CommentRepository {
    Comment save(Comment comment);
    Optional<Comment> findByIdAndProjectId(UUID id, UUID projectId);
    List<Comment> findByThreadId(UUID threadId);
}
