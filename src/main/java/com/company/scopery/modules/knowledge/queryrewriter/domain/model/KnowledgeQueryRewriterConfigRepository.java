package com.company.scopery.modules.knowledge.queryrewriter.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface KnowledgeQueryRewriterConfigRepository {
    Optional<KnowledgeQueryRewriterConfig> findByWorkspaceId(UUID workspaceId);
    KnowledgeQueryRewriterConfig save(KnowledgeQueryRewriterConfig config);
}
