package com.company.scopery.modules.aiplanning.suggestion.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiPlanningSuggestionRepository {
    AiPlanningSuggestion save(AiPlanningSuggestion suggestion);
    Optional<AiPlanningSuggestion> findById(UUID id);
    Optional<AiPlanningSuggestion> findByIdAndProjectId(UUID id, UUID projectId);
    List<AiPlanningSuggestion> findByProjectId(UUID projectId);
    List<AiPlanningSuggestion> findByPlanningRunId(UUID planningRunId);
}
