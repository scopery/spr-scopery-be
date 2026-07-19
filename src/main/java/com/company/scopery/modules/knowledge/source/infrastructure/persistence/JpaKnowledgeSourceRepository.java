package com.company.scopery.modules.knowledge.source.infrastructure.persistence;

import com.company.scopery.modules.knowledge.source.domain.enums.KnowledgeSourceType;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeSource;
import com.company.scopery.modules.knowledge.source.domain.model.KnowledgeSourceRepository;
import com.company.scopery.modules.knowledge.source.infrastructure.mapper.KnowledgeSourcePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaKnowledgeSourceRepository implements KnowledgeSourceRepository {

    private final SpringDataKnowledgeSourceJpaRepository springData;
    private final KnowledgeSourcePersistenceMapper mapper;

    public JpaKnowledgeSourceRepository(SpringDataKnowledgeSourceJpaRepository springData,
                                         KnowledgeSourcePersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public KnowledgeSource save(KnowledgeSource source) {
        KnowledgeSourceJpaEntity entity = mapper.toJpaEntity(source);
        KnowledgeSourceJpaEntity saved = springData.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<KnowledgeSource> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<KnowledgeSource> findByWorkspaceAndTypeAndRef(UUID workspaceId,
                                                                   KnowledgeSourceType sourceType,
                                                                   UUID sourceRefId,
                                                                   UUID sourceVersionRefId) {
        return springData.findByWorkspaceIdAndSourceTypeAndSourceRefIdAndSourceVersionRefId(
                workspaceId, sourceType.name(), sourceRefId, sourceVersionRefId
        ).map(mapper::toDomain);
    }

    @Override
    public List<KnowledgeSource> findByWorkspaceId(UUID workspaceId) {
        return springData.findByWorkspaceId(workspaceId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<KnowledgeSource> findByProjectId(UUID projectId) {
        return springData.findByProjectId(projectId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        springData.deleteById(id);
    }
}
