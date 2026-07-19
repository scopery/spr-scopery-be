package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import com.company.scopery.modules.airecommendation.domain.model.SuggestionSchemaDefinition;
import com.company.scopery.modules.airecommendation.domain.repository.SuggestionSchemaDefinitionRepository;
import com.company.scopery.modules.airecommendation.infrastructure.mapper.SuggestionSchemaDefinitionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaSuggestionSchemaDefinitionRepository implements SuggestionSchemaDefinitionRepository {

    private final SpringDataSuggestionSchemaDefinitionJpaRepository springDataRepository;
    private final SuggestionSchemaDefinitionPersistenceMapper mapper;

    public JpaSuggestionSchemaDefinitionRepository(
            SpringDataSuggestionSchemaDefinitionJpaRepository springDataRepository,
            SuggestionSchemaDefinitionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public SuggestionSchemaDefinition save(SuggestionSchemaDefinition schema) {
        SuggestionSchemaDefinitionJpaEntity entity = mapper.toJpaEntity(schema);
        SuggestionSchemaDefinitionJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<SuggestionSchemaDefinition> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<SuggestionSchemaDefinition> findByCodeAndVersion(String code, int version) {
        return springDataRepository.findByCodeAndSchemaVersion(code, version).map(mapper::toDomain);
    }

    @Override
    public boolean existsByCodeAndVersion(String code, int version) {
        return springDataRepository.existsByCodeAndSchemaVersion(code, version);
    }
}
