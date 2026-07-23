package com.company.scopery.modules.knowledge.queryrewriter.infrastructure.persistence;

import com.company.scopery.modules.knowledge.queryrewriter.domain.model.KnowledgeQueryRewriterConfig;
import com.company.scopery.modules.knowledge.queryrewriter.domain.model.KnowledgeQueryRewriterConfigRepository;
import com.company.scopery.modules.knowledge.queryrewriter.infrastructure.mapper.KnowledgeQueryRewriterConfigPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaKnowledgeQueryRewriterConfigRepository implements KnowledgeQueryRewriterConfigRepository {

    private final SpringDataKnowledgeQueryRewriterConfigJpaRepository springData;
    private final KnowledgeQueryRewriterConfigPersistenceMapper mapper;

    public JpaKnowledgeQueryRewriterConfigRepository(
            SpringDataKnowledgeQueryRewriterConfigJpaRepository springData,
            KnowledgeQueryRewriterConfigPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public Optional<KnowledgeQueryRewriterConfig> findByWorkspaceId(UUID workspaceId) {
        return springData.findByWorkspaceId(workspaceId).map(mapper::toDomain);
    }

    @Override
    public KnowledgeQueryRewriterConfig save(KnowledgeQueryRewriterConfig config) {
        KnowledgeQueryRewriterConfigJpaEntity entity = mapper.toJpaEntity(config);
        KnowledgeQueryRewriterConfigJpaEntity saved = springData.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }
}
