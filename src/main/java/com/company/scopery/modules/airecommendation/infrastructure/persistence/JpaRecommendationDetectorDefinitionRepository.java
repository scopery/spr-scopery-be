package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import com.company.scopery.modules.airecommendation.domain.model.RecommendationDetectorDefinition;
import com.company.scopery.modules.airecommendation.domain.repository.RecommendationDetectorDefinitionRepository;
import com.company.scopery.modules.airecommendation.infrastructure.mapper.RecommendationDetectorDefinitionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRecommendationDetectorDefinitionRepository implements RecommendationDetectorDefinitionRepository {

    private final SpringDataRecommendationDetectorDefinitionJpaRepository springDataRepository;
    private final RecommendationDetectorDefinitionPersistenceMapper mapper;

    public JpaRecommendationDetectorDefinitionRepository(
            SpringDataRecommendationDetectorDefinitionJpaRepository springDataRepository,
            RecommendationDetectorDefinitionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public RecommendationDetectorDefinition save(RecommendationDetectorDefinition detector) {
        RecommendationDetectorDefinitionJpaEntity entity = mapper.toJpaEntity(detector);
        RecommendationDetectorDefinitionJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<RecommendationDetectorDefinition> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<RecommendationDetectorDefinition> findActiveByCode(String code) {
        return springDataRepository.findActiveByCode(code).map(mapper::toDomain);
    }

    @Override
    public List<RecommendationDetectorDefinition> findAllActiveByPackCode(String packCode) {
        return springDataRepository.findAllActiveByPackCode(packCode)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByCodeAndVersion(String code, int version) {
        return springDataRepository.existsByCodeAndVersion(code, version);
    }
}
