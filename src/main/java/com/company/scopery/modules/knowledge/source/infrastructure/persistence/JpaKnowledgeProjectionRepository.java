package com.company.scopery.modules.knowledge.source.infrastructure.persistence;

import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeProjection;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeProjectionRepository;
import com.company.scopery.modules.knowledge.source.infrastructure.mapper.KnowledgeProjectionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaKnowledgeProjectionRepository implements KnowledgeProjectionRepository {

    private final SpringDataKnowledgeProjectionJpaRepository springData;
    private final KnowledgeProjectionPersistenceMapper mapper;

    public JpaKnowledgeProjectionRepository(SpringDataKnowledgeProjectionJpaRepository springData,
                                             KnowledgeProjectionPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public KnowledgeProjection save(KnowledgeProjection projection) {
        KnowledgeProjectionJpaEntity entity = mapper.toJpaEntity(projection);
        KnowledgeProjectionJpaEntity saved = springData.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<KnowledgeProjection> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<KnowledgeProjection> findLatestBySourceId(UUID sourceId) {
        return springData.findLatestBySourceId(sourceId).map(mapper::toDomain);
    }

    @Override
    public List<KnowledgeProjection> findBySourceId(UUID sourceId) {
        return springData.findBySourceIdOrderByProjectionVersionDesc(sourceId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public int nextProjectionVersion(UUID sourceId) {
        return springData.maxProjectionVersion(sourceId) + 1;
    }
}
