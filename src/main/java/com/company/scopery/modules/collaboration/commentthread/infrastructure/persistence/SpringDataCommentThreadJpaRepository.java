package com.company.scopery.modules.collaboration.commentthread.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataCommentThreadJpaRepository extends JpaRepository<CommentThreadJpaEntity, UUID> {
    Optional<CommentThreadJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<CommentThreadJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    List<CommentThreadJpaEntity> findByProjectIdAndTargetTypeAndTargetIdOrderByCreatedAtDesc(UUID projectId, String targetType, UUID targetId);
}
