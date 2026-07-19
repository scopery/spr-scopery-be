package com.company.scopery.modules.knowledge.graph.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataKnowledgeGraphNodeJpaRepository extends JpaRepository<KnowledgeGraphNodeJpaEntity, UUID> {
    List<KnowledgeGraphNodeJpaEntity> findByWorkspaceId(UUID workspaceId);
    Optional<KnowledgeGraphNodeJpaEntity> findByWorkspaceIdAndNodeTypeAndSourceRefIdAndSourceVersionRefId(
            UUID workspaceId, String nodeType, UUID sourceRefId, UUID sourceVersionRefId);
}
