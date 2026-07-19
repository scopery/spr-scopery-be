package com.company.scopery.modules.aiplanning.suggestion.infrastructure.persistence;

import com.company.scopery.modules.aiplanning.suggestion.domain.model.AiPlanningSuggestion;
import com.company.scopery.modules.aiplanning.suggestion.domain.model.AiPlanningSuggestionRepository;
import com.company.scopery.modules.aiplanning.suggestion.infrastructure.mapper.AiPlanningSuggestionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiPlanningSuggestionRepository implements AiPlanningSuggestionRepository {
    private final SpringDataAiPlanningSuggestionJpaRepository springData;
    private final AiPlanningSuggestionPersistenceMapper mapper;

    public JpaAiPlanningSuggestionRepository(SpringDataAiPlanningSuggestionJpaRepository springData,
                                             AiPlanningSuggestionPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override public AiPlanningSuggestion save(AiPlanningSuggestion s) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(s)));
    }
    @Override public Optional<AiPlanningSuggestion> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }
    @Override public Optional<AiPlanningSuggestion> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<AiPlanningSuggestion> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
    @Override public List<AiPlanningSuggestion> findByPlanningRunId(UUID planningRunId) {
        return springData.findByPlanningRunIdOrderByCreatedAtDesc(planningRunId).stream().map(mapper::toDomain).toList();
    }
}
