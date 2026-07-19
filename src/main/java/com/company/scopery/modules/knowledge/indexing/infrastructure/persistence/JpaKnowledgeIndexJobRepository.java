package com.company.scopery.modules.knowledge.indexing.infrastructure.persistence;

import com.company.scopery.modules.knowledge.indexing.domain.enums.IndexJobStatus;
import com.company.scopery.modules.knowledge.indexing.domain.model.KnowledgeIndexJob;
import com.company.scopery.modules.knowledge.indexing.domain.model.KnowledgeIndexJobRepository;
import com.company.scopery.modules.knowledge.indexing.infrastructure.mapper.KnowledgeIndexJobPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaKnowledgeIndexJobRepository implements KnowledgeIndexJobRepository {

    private final SpringDataKnowledgeIndexJobJpaRepository springData;
    private final KnowledgeIndexJobPersistenceMapper mapper;

    public JpaKnowledgeIndexJobRepository(SpringDataKnowledgeIndexJobJpaRepository springData,
                                           KnowledgeIndexJobPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public KnowledgeIndexJob save(KnowledgeIndexJob job) {
        KnowledgeIndexJobJpaEntity entity = mapper.toJpaEntity(job);
        KnowledgeIndexJobJpaEntity saved = springData.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<KnowledgeIndexJob> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<KnowledgeIndexJob> findByIdempotencyKey(String idempotencyKey) {
        return springData.findByIdempotencyKey(idempotencyKey).map(mapper::toDomain);
    }

    @Override
    public List<KnowledgeIndexJob> findByWorkspaceId(UUID workspaceId) {
        return springData.findByWorkspaceId(workspaceId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<KnowledgeIndexJob> findByStatus(IndexJobStatus status) {
        return springData.findByJobStatus(status.name()).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
