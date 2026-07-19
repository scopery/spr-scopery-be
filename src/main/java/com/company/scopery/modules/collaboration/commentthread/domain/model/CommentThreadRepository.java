package com.company.scopery.modules.collaboration.commentthread.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface CommentThreadRepository {
    CommentThread save(CommentThread thread);
    Optional<CommentThread> findByIdAndProjectId(UUID id, UUID projectId);
    List<CommentThread> findByProjectId(UUID projectId);
    List<CommentThread> findByTarget(UUID projectId, String targetType, UUID targetId);
}
