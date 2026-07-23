package com.company.scopery.modules.knowledge.queryrewriter.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataKnowledgeQueryRewriterConfigJpaRepository
        extends JpaRepository<KnowledgeQueryRewriterConfigJpaEntity, UUID> {

    Optional<KnowledgeQueryRewriterConfigJpaEntity> findByWorkspaceId(UUID workspaceId);
}
