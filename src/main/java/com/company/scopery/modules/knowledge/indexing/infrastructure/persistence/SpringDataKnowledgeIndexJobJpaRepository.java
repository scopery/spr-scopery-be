package com.company.scopery.modules.knowledge.indexing.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataKnowledgeIndexJobJpaRepository extends JpaRepository<KnowledgeIndexJobJpaEntity, UUID> {
    Optional<KnowledgeIndexJobJpaEntity> findByIdempotencyKey(String idempotencyKey);
    List<KnowledgeIndexJobJpaEntity> findByWorkspaceId(UUID workspaceId);
    List<KnowledgeIndexJobJpaEntity> findByJobStatus(String jobStatus);
}
