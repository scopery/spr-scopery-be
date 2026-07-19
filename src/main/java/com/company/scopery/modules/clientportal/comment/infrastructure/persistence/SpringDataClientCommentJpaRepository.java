package com.company.scopery.modules.clientportal.comment.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataClientCommentJpaRepository extends JpaRepository<ClientCommentJpaEntity, UUID> {
    List<ClientCommentJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    List<ClientCommentJpaEntity> findByProjectIdAndTargetTypeAndTargetIdOrderByCreatedAtDesc(UUID projectId, String targetType, UUID targetId);
}
