package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import com.company.scopery.modules.airecommendation.domain.model.RecommendationPackDefinition;
import com.company.scopery.modules.airecommendation.domain.repository.RecommendationPackDefinitionRepository;
import com.company.scopery.modules.airecommendation.infrastructure.mapper.RecommendationPackDefinitionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRecommendationPackDefinitionRepository implements RecommendationPackDefinitionRepository {

    private final SpringDataRecommendationPackDefinitionJpaRepository springDataRepository;
    private final RecommendationPackDefinitionPersistenceMapper mapper;

    public JpaRecommendationPackDefinitionRepository(
            SpringDataRecommendationPackDefinitionJpaRepository springDataRepository,
            RecommendationPackDefinitionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public RecommendationPackDefinition save(RecommendationPackDefinition pack) {
        RecommendationPackDefinitionJpaEntity entity = mapper.toJpaEntity(pack);
        RecommendationPackDefinitionJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<RecommendationPackDefinition> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<RecommendationPackDefinition> findActiveByCode(String code) {
        return springDataRepository.findActiveByCode(code).map(mapper::toDomain);
    }

    @Override
    public List<RecommendationPackDefinition> findAllActive() {
        return springDataRepository.findAllActive().stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByCodeAndVersion(String code, int version) {
        return springDataRepository.existsByCodeAndVersion(code, version);
    }
}
