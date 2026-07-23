package com.company.scopery.modules.knowledge.source.domain.model;

import com.company.scopery.modules.knowledge.source.domain.enums.KnowledgeSourceStatus;
import com.company.scopery.modules.knowledge.source.domain.enums.KnowledgeSourceType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KnowledgeSourceRepository {
    KnowledgeSource save(KnowledgeSource source);
    int markIndexed(UUID id, KnowledgeSourceStatus status, Instant indexedAt);
    Optional<KnowledgeSource> findById(UUID id);
    Optional<KnowledgeSource> findByWorkspaceAndTypeAndRef(UUID workspaceId, KnowledgeSourceType sourceType, UUID sourceRefId, UUID sourceVersionRefId);
    List<KnowledgeSource> findByWorkspaceId(UUID workspaceId);
    List<KnowledgeSource> findByProjectId(UUID projectId);
    void deleteById(UUID id);
}
