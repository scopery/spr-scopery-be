package com.company.scopery.modules.knowledge.indexing.infrastructure.persistence;

import com.company.scopery.modules.knowledge.indexing.domain.enums.IndexDefinitionStatus;
import com.company.scopery.modules.knowledge.indexing.domain.model.KnowledgeIndexDefinition;
import com.company.scopery.modules.knowledge.indexing.domain.model.KnowledgeIndexDefinitionRepository;
import com.company.scopery.modules.knowledge.indexing.infrastructure.mapper.KnowledgeIndexDefinitionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaKnowledgeIndexDefinitionRepository implements KnowledgeIndexDefinitionRepository {

    private final SpringDataKnowledgeIndexDefinitionJpaRepository springData;
    private final KnowledgeIndexDefinitionPersistenceMapper mapper;

    public JpaKnowledgeIndexDefinitionRepository(SpringDataKnowledgeIndexDefinitionJpaRepository springData,
                                                  KnowledgeIndexDefinitionPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public KnowledgeIndexDefinition save(KnowledgeIndexDefinition definition) {
        KnowledgeIndexDefinitionJpaEntity entity = mapper.toJpaEntity(definition);
        KnowledgeIndexDefinitionJpaEntity saved = springData.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<KnowledgeIndexDefinition> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<KnowledgeIndexDefinition> findByCode(String code) {
        return springData.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public List<KnowledgeIndexDefinition> findByEnvironmentAndStatus(String environment, IndexDefinitionStatus status) {
        return springData.findByEnvironmentAndDefinitionStatus(environment, status.name()).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
