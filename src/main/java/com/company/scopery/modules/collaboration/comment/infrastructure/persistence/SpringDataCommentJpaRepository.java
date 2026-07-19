package com.company.scopery.modules.collaboration.comment.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataCommentJpaRepository extends JpaRepository<CommentJpaEntity, UUID> {
    Optional<CommentJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<CommentJpaEntity> findByThreadIdOrderByCreatedAtAsc(UUID threadId);
}
