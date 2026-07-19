package com.company.scopery.modules.knowledge.source.domain.model;

import java.util.List;
import java.util.UUID;

public interface KnowledgeChunkRepository {
    KnowledgeChunk save(KnowledgeChunk chunk);
    List<KnowledgeChunk> saveAll(List<KnowledgeChunk> chunks);
    List<KnowledgeChunk> findCurrentBySourceId(UUID sourceId);
    List<KnowledgeChunk> findByProjectionId(UUID projectionId);
    void markSupersededBySourceId(UUID sourceId);
}
