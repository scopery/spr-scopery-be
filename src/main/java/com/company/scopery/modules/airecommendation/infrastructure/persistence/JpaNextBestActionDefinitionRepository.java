package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import com.company.scopery.modules.airecommendation.domain.model.NextBestActionDefinition;
import com.company.scopery.modules.airecommendation.domain.repository.NextBestActionDefinitionRepository;
import com.company.scopery.modules.airecommendation.infrastructure.mapper.NextBestActionDefinitionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaNextBestActionDefinitionRepository implements NextBestActionDefinitionRepository {

    private final SpringDataNextBestActionDefinitionJpaRepository springDataRepository;
    private final NextBestActionDefinitionPersistenceMapper mapper;

    public JpaNextBestActionDefinitionRepository(
            SpringDataNextBestActionDefinitionJpaRepository springDataRepository,
            NextBestActionDefinitionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public NextBestActionDefinition save(NextBestActionDefinition nba) {
        NextBestActionDefinitionJpaEntity entity = mapper.toJpaEntity(nba);
        NextBestActionDefinitionJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<NextBestActionDefinition> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<NextBestActionDefinition> findAllActive() {
        return springDataRepository.findAllActive().stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByCodeAndVersion(String code, int version) {
        return springDataRepository.existsByCodeAndVersion(code, version);
    }
}
