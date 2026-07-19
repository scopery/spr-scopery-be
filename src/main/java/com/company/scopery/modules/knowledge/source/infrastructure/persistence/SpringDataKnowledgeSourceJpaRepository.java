package com.company.scopery.modules.knowledge.source.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataKnowledgeSourceJpaRepository extends JpaRepository<KnowledgeSourceJpaEntity, UUID> {
    Optional<KnowledgeSourceJpaEntity> findByWorkspaceIdAndSourceTypeAndSourceRefIdAndSourceVersionRefId(
            UUID workspaceId, String sourceType, UUID sourceRefId, UUID sourceVersionRefId);
    java.util.List<KnowledgeSourceJpaEntity> findByWorkspaceId(UUID workspaceId);
    java.util.List<KnowledgeSourceJpaEntity> findByProjectId(UUID projectId);
}
