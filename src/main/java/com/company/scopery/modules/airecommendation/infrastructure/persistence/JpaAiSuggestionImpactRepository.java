package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionImpact;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionImpactRepository;
import com.company.scopery.modules.airecommendation.infrastructure.mapper.AiSuggestionImpactPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaAiSuggestionImpactRepository implements AiSuggestionImpactRepository {

    private final SpringDataAiSuggestionImpactJpaRepository springDataRepository;
    private final AiSuggestionImpactPersistenceMapper mapper;

    public JpaAiSuggestionImpactRepository(
            SpringDataAiSuggestionImpactJpaRepository springDataRepository,
            AiSuggestionImpactPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiSuggestionImpact save(AiSuggestionImpact impact) {
        AiSuggestionImpactJpaEntity entity = mapper.toJpaEntity(impact);
        AiSuggestionImpactJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<AiSuggestionImpact> saveAll(List<AiSuggestionImpact> impacts) {
        List<AiSuggestionImpactJpaEntity> entities = impacts.stream().map(mapper::toJpaEntity).toList();
        List<AiSuggestionImpactJpaEntity> saved = springDataRepository.saveAllAndFlush(entities);
        return saved.stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AiSuggestionImpact> findBySuggestionId(UUID suggestionId) {
        return springDataRepository.findBySuggestionId(suggestionId)
                .stream().map(mapper::toDomain).toList();
    }
}
