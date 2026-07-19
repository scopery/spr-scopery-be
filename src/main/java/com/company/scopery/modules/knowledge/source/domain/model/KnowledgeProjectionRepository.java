package com.company.scopery.modules.knowledge.source.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KnowledgeProjectionRepository {
    KnowledgeProjection save(KnowledgeProjection projection);
    Optional<KnowledgeProjection> findById(UUID id);
    Optional<KnowledgeProjection> findLatestBySourceId(UUID sourceId);
    List<KnowledgeProjection> findBySourceId(UUID sourceId);
    int nextProjectionVersion(UUID sourceId);
}
