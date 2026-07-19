package com.company.scopery.modules.knowledge.source.infrastructure.persistence;

import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeChunk;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeChunkRepository;
import com.company.scopery.modules.knowledge.source.infrastructure.mapper.KnowledgeChunkPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaKnowledgeChunkRepository implements KnowledgeChunkRepository {

    private final SpringDataKnowledgeChunkJpaRepository springData;
    private final KnowledgeChunkPersistenceMapper mapper;

    public JpaKnowledgeChunkRepository(SpringDataKnowledgeChunkJpaRepository springData,
                                        KnowledgeChunkPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public KnowledgeChunk save(KnowledgeChunk chunk) {
        KnowledgeChunkJpaEntity entity = mapper.toJpaEntity(chunk);
        KnowledgeChunkJpaEntity saved = springData.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<KnowledgeChunk> saveAll(List<KnowledgeChunk> chunks) {
        List<KnowledgeChunkJpaEntity> entities = chunks.stream()
                .map(mapper::toJpaEntity)
                .toList();
        return springData.saveAllAndFlush(entities).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<KnowledgeChunk> findCurrentBySourceId(UUID sourceId) {
        return springData.findBySourceIdAndIsCurrentTrueOrderByChunkOrdinalAsc(sourceId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<KnowledgeChunk> findByProjectionId(UUID projectionId) {
        return springData.findByProjectionIdOrderByChunkOrdinalAsc(projectionId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void markSupersededBySourceId(UUID sourceId) {
        springData.markSupersededBySourceId(sourceId);
    }
}
