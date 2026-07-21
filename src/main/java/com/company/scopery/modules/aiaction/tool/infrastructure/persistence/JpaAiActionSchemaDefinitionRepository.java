package com.company.scopery.modules.aiaction.tool.infrastructure.persistence;

import com.company.scopery.modules.aiaction.tool.domain.model.AiActionSchemaDefinition;
import com.company.scopery.modules.aiaction.tool.domain.model.AiActionSchemaDefinitionRepository;
import com.company.scopery.modules.aiaction.tool.infrastructure.mapper.AiActionSchemaDefinitionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaAiActionSchemaDefinitionRepository implements AiActionSchemaDefinitionRepository {

    private final SpringDataAiActionSchemaDefinitionJpaRepository springDataRepository;
    private final AiActionSchemaDefinitionPersistenceMapper mapper;

    public JpaAiActionSchemaDefinitionRepository(SpringDataAiActionSchemaDefinitionJpaRepository springDataRepository,
                                                  AiActionSchemaDefinitionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiActionSchemaDefinition save(AiActionSchemaDefinition schema) {
        AiActionSchemaDefinitionJpaEntity entity = mapper.toJpaEntity(schema);
        AiActionSchemaDefinitionJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AiActionSchemaDefinition> findBySchemaCodeAndVersion(String schemaCode, int schemaVersion) {
        return springDataRepository.findBySchemaCodeAndSchemaVersion(schemaCode, schemaVersion)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsBySchemaCodeAndVersion(String schemaCode, int schemaVersion) {
        return springDataRepository.existsBySchemaCodeAndSchemaVersion(schemaCode, schemaVersion);
    }
}
